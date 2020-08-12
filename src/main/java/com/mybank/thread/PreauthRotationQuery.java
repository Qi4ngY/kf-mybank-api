package com.mybank.thread;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayFundAuthOperationDetailQueryRequest;
import com.alipay.api.response.AlipayFundAuthOperationDetailQueryResponse;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.base.entity.*;
import com.mybank.base.entity.constant.AlipayPreAuthFundsFlowType;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.base.repository.AlipayPreAuthFundsFlowRepository;
import com.mybank.base.repository.HbCmPreOrderReceiveRecordRepository;
import com.mybank.base.repository.HnCuPreOrderReceiveRecordRepository;
import com.mybank.base.repository.HzChinatelecomPreOrderReceiveRecordRepository;
import com.mybank.config.RedisUtils;
import com.mybank.config.SpringUtil;
import com.mybank.exception.ThreadExceptionLoggerRunnable;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AlipayPreAuthService;
import com.taobao.api.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 * 支付宝请求发送成功后轮流查询支付宝订单状态
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/22
 */
public class PreauthRotationQuery implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(PreauthRotationQuery.class);

    private static String[] accountPeriods = {"201808", "201809", "201810", "201811", "201812",
            "201901", "201902", "201903", "201904", "201905", "201906", "201907", "201908", "201909", "201910", "201911", "201912",
            "202001", "202002", "202003", "202004", "202005", "202006", "202007", "202008", "202009", "202010", "202011", "202012",
            "202101", "202102", "202103", "202104", "202105", "202106", "202107", "202108", "202109", "202110", "202111", "202112",
            "202201", "202202", "202203", "202204", "202205", "202206", "202207", "202208", "202209", "202210", "202211", "202212",
            "202301", "202302", "202303", "202304", "202305", "202306", "202307", "202308", "202309", "203210", "202311", "202312",
            "202401", "202402", "202403", "202404", "202405", "202406", "202407", "202408", "202409", "202410", "202411", "202412"};

	private long orderNo;

	private String alipayAppId;

	private int times;

	private String uuid;

	public PreauthRotationQuery(String alipayAppId, long orderNo,String uuid) {
		this.alipayAppId = alipayAppId;
		this.orderNo = orderNo;
		this.uuid = uuid;
	}

	private PreauthRotationQuery(String alipayAppId, long orderNo,String uuid,int times) {
		this.alipayAppId = alipayAppId;
		this.orderNo = orderNo;
		this.uuid = uuid;
		this.times = times;
	}

	@Override
	public void run() {
		try {
			logger.info("开始轮询预授权订单{}",orderNo);
			AlipayFundAuthOperationDetailQueryRequest request = new AlipayFundAuthOperationDetailQueryRequest();
			request.setBizContent("{" +
					"\"out_order_no\":\""+orderNo+"\"," +
					"\"out_request_no\":\"" + orderNo +"\"" +
					"  }");
			AlipayFundAuthOperationDetailQueryResponse response = AlipayXmlConfigs.getAlipayClientModel(alipayAppId).getAlipayClient().execute(request);
			AlipayPreAuthService service = SpringUtil.getBean(AlipayPreAuthService.class);
			BaseOrder<AlipayTradeOrderDetail> order = service.find(orderNo);
			if(response.isSuccess()){
				if(order != null){
					if(order.getStatus() == OrderStatus.FREEZE.getCode()){
						logger.info("轮询预授权订单{}，已回掉成功",orderNo);
						return;
					}
					if("SUCCESS".equals(response.getStatus())){
						if(SpringUtil.getBean(RedisUtils.class).setIfAbsent("kf:alipay:preauth:notify:"+uuid, "uuid",10,TimeUnit.SECONDS)){
							logger.info("轮询预授权订单{}，修改订单数据和状态",orderNo);
							Map<String, String> params = new HashMap<>();
							params.put("auth_no",response.getAuthNo());
							params.put("operation_id",response.getOperationId());
							Future future = ThreadPoolManager.executorService.submit(new ModifyDetail(orderNo, params));
							future.get(5,TimeUnit.SECONDS);
							ThreadPoolManager.scheduledExecutorService.schedule(new PreauthCallBackNotify(orderNo, 0), 0, TimeUnit.SECONDS);
							logger.error("预授权订单号：{}轮询发送MQ。",orderNo);
						}
					}else if("CLOSED".equals(response.getStatus())){
						logger.info("轮询预授权订单{}，订单已关闭",orderNo);
                        order.setStatus(OrderStatus.CLOSED.getCode());
                        service.saveOrder(order);
						return;
					}else{
						logger.info("轮询预授权订单{}，用户已扫码，还没有完成支付",orderNo);
						if(times < 15) {
							ThreadPoolManager.scheduledExecutorService.schedule(new PreauthRotationQuery(alipayAppId, orderNo,uuid, times+1), 10, TimeUnit.SECONDS);
						}else{
                            order.setStatus(OrderStatus.CLOSED.getCode());
                            service.saveOrder(order);
							logger.info("开始轮询预授权订单{},订单超时！",orderNo);
						}
					}
				}
			}else{
				logger.info("开始轮询预授权订单{},查询接口异常！{}",orderNo,response.getSubMsg());
				if(times < 15) {
					ThreadPoolManager.scheduledExecutorService.schedule(new PreauthRotationQuery(alipayAppId, orderNo,uuid, times+1), 10, TimeUnit.SECONDS);
				}else{
				    if(order != null){
                        order.setStatus(OrderStatus.CLOSED.getCode());
                        service.saveOrder(order);
                    }
					logger.info("开始轮询预授权订单{},订单超时！",orderNo);
				}

			}
			logger.info("结束轮询预授权订单{}",orderNo);
		}catch (Exception e){
			logger.info("轮询预授权订单{}异常",e);
		}
	}

    public static class ModifyDetail implements Runnable {
        private long orderNo;

        private Map<String, String> params;

        ModifyDetail(long orderNo, Map<String, String> params) {
            this.orderNo = orderNo;
            this.params = params;
        }

        @Override
        public void run() {
            try {
                //判断订单是否已回掉
                if (SpringUtil.getBean(RedisUtils.class).setIfAbsent("kf:alipay:pre:auth:callback:" + orderNo, String.valueOf(orderNo), 240, TimeUnit.HOURS)) {
                    final AlipayPreAuthService service = SpringUtil.getBean(AlipayPreAuthService.class);
                    BaseOrder<AlipayPreAuthOrderDetail> baseOrder = service.find(orderNo);
                    if (baseOrder == null || baseOrder.getDetail() == null) {
                        return;
                    }

                    if (baseOrder.getStatus() == OrderStatus.FREEZE.getCode()) {
                        return;
                    }

                    AlipayClient alipayClient = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId()).getAlipayClient();
                    AlipayFundAuthOperationDetailQueryRequest request = new AlipayFundAuthOperationDetailQueryRequest();
                    request.setBizContent("{" +
                            "\"auth_no\":\"" + params.get("auth_no") + "\"" +
                            ",\"operation_id\":\"" + params.get("operation_id") + "\"" +
                            "  }");
                    AlipayFundAuthOperationDetailQueryResponse response = alipayClient.execute(request);

                    baseOrder.setStatus(OrderStatus.FREEZE.getCode());
                    AlipayPreAuthOrderDetail detail = baseOrder.getDetail();
                    detail.setAlipayOrderNo(response.getOperationId());
                    detail.setAuthNo(response.getAuthNo());
                    detail.setPayerLogonId(response.getPayerLogonId());
                    detail.setPayerUserId(response.getPayerUserId());
                    detail.setRestAmount(new BigDecimal(response.getRestAmount()));
                    detail.setTotalPayAmount(new BigDecimal(response.getTotalPayAmount()));
                    detail.setAuthTime(response.getGmtTrans());
                    service.saveOrder(baseOrder);
                    AlipayPreAuthFundsFlow flow = new AlipayPreAuthFundsFlow();
                    flow.setFlowId(AlipayPreAuthFundsFlow.nextFlowId());
                    flow.setType(AlipayPreAuthFundsFlowType.FREEZE.getCode());
                    flow.setRestAmount(detail.getRestAmount());
                    flow.setAlipayOrderNo(detail.getAlipayOrderNo());
                    flow.setAmount(detail.getRestAmount());
                    flow.setOrderNo(baseOrder.getOrderNo());
                    SpringUtil.getBean(AlipayPreAuthFundsFlowRepository.class).save(flow);
                    logger.info("预授权业务类型：{}，订单号：{}", baseOrder.getDetail().getBiz(), baseOrder.getOrderNo());
                    if ("jx_cu".equals(baseOrder.getDetail().getBiz())) {
                        //预授权》江西联通
                        logger.info("江西联通业务无后续处理...");
                    } else if ("hz_ct".equals(baseOrder.getDetail().getBiz())) {
                        //预授权》杭州电信
                        ThreadPoolManager.scheduledExecutorService.schedule(() -> {
                            logger.info("开始处理杭州电信预授权业务，订单号：{}", baseOrder.getOrderNo());
                            hzCtDeal(baseOrder,service);
                        },1,TimeUnit.SECONDS);
                    }else if ("hb_cm".equals(baseOrder.getDetail().getBiz())) {
                        //预授权》湖北移动
                        ThreadPoolManager.scheduledExecutorService.schedule(() -> {
                            logger.info("开始处理预授权湖北移动业务，订单号：{}", baseOrder.getOrderNo());
                            hbCmDeal(baseOrder,service);
                        },1,TimeUnit.SECONDS);
                    }else if("hn_cu".equals(baseOrder.getDetail().getBiz())){
                        //预授权》湖南联通
                        logger.info("预授权湖南联通无后续处理...");
                    }
                }
            } catch (Exception e) {
                logger.error("预授权修改详情失败：", e);
                ThreadPoolManager.executorService.execute(new ThreadExceptionLoggerRunnable("授权修改详情失败：:", e));
            }
        }
    }

//    private static void hnCuDeal(BaseOrder<AlipayPreAuthOrderDetail> baseOrder){
//
//        logger.info("开始处理【预授权·湖南联通】业务，订单号：{}", baseOrder.getOrderNo());
//        BaseOrder<AlipayPreAuthOrderDetail> baseOrder1 = SpringUtil.getBean(AlipayPreAuthService.class).find(baseOrder.getOrderNo());
//        try {
//            List<HnCuPreOrderReceiveRecord> list = new ArrayList<>();
//            String effectTime = new SimpleDateFormat("yyyyMM").format(baseOrder.getTradeDate());
//            int stepNum = baseOrder.getDetail().getAuthStep();
//            //交易下月第一个账期
//            int index = Arrays.binarySearch(accountPeriods, effectTime) + 1;
//            int last = index+stepNum;
//            HnCuPreOrderReceiveRecord record;
//            for (int i = 0; i < baseOrder1.getDetail().getAuthStep(); i++) {
//                record = new HnCuPreOrderReceiveRecord();
//                record.setOrderNo(baseOrder1.getOrderNo());
//                record.setAccountPeriod(accountPeriods[index++]);
//                record.setStep(i+1);
//                record.setUnfreezeStatus(HnCuPreOrderReceiveRecord.UnfreezeStatus.UNFREEZE.isCode());
//                //最后1个月结算前3个月
//                if(i + 3 > stepNum){
//                    record.setExpireTime(parseDate(accountPeriods[last]));
//                }else {
//                    record.setExpireTime(parseDate(accountPeriods[index+3]));
//                }
//                list.add(record);
//            }
//            HnCuPreOrderReceiveRecordRepository hbCmPreOrderReceiveRecordRepository = SpringUtil.getBean(HnCuPreOrderReceiveRecordRepository.class);
//            hbCmPreOrderReceiveRecordRepository.saveAll(list);
//        } catch (Exception e) {
//            logger.error("【预授权·湖南联通】处理预授权首期转支付异常：", e);
//        }
//        logger.info("结束处理【预授权·湖南联通】业务，订单号：{}", baseOrder1.getOrderNo());
//
//    }

    private static Date parseDate(String yyyyMM) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd").parse(yyyyMM+"01");
    }

    private static void hbCmDeal(BaseOrder<AlipayPreAuthOrderDetail> baseOrder,AlipayPreAuthService service){

        logger.info("开始处理【预授权·湖北移动】业务，订单号：{}", baseOrder.getOrderNo());
        BaseOrder<AlipayPreAuthOrderDetail> baseOrder1 = SpringUtil.getBean(AlipayPreAuthService.class).find(baseOrder.getOrderNo());
        try {
            List<HbCmPreOrderReceiveRecord> list = new ArrayList<>();
            String effectTime = new SimpleDateFormat("yyyyMM").format(baseOrder.getTradeDate());
            int index = Arrays.binarySearch(accountPeriods, effectTime);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            BigDecimal totalAmount = baseOrder1.getTotalAmount().divide(new BigDecimal(baseOrder1.getDetail().getAuthStep()), 2, BigDecimal.ROUND_UP);
            HbCmPreOrderReceiveRecord record;
            for (int i = 0; i < baseOrder1.getDetail().getAuthStep(); i++) {
                record = new HbCmPreOrderReceiveRecord();
                record.setOrderNo(baseOrder1.getOrderNo());
                record.setAccountPeriod(accountPeriods[index++]);
                record.setStep(i+1);
                record.setPayMoney(totalAmount);
                if (i == 0) {
                    //处理第一期转支付金额
                    JSONObject jsonObject = service.freezePay(totalAmount, baseOrder1.getOrderNo());
                    if(jsonObject!=null){
                        record.setFlowId(jsonObject.getLong("flowId"));
                        record.setSellerId(jsonObject.getString("sellerId"));
                        record.setPayStatus(HbCmPreOrderReceiveRecord.PayStatus.PAYED.isCode());
                    }
                }
                record.setExpireDate(format.parse(record.getAccountPeriod()+"01"));
                list.add(record);
            }
            HbCmPreOrderReceiveRecordRepository hbCmPreOrderReceiveRecordRepository = SpringUtil.getBean(HbCmPreOrderReceiveRecordRepository.class);
            hbCmPreOrderReceiveRecordRepository.saveAll(list);
        } catch (Exception e) {
            logger.error("【预授权·湖北移动】处理预授权首期转支付异常：", e);
        }
        logger.info("结束处理【预授权·湖北移动】业务，订单号：{}", baseOrder1.getOrderNo());

    }

    private static void hzCtDeal(BaseOrder<AlipayPreAuthOrderDetail> baseOrder,AlipayPreAuthService service){
        logger.info("开始处理杭州电信预授权业务，订单号：{}", baseOrder.getOrderNo());
        BaseOrder<AlipayPreAuthOrderDetail> baseOrder1 = SpringUtil.getBean(AlipayPreAuthService.class).find(baseOrder.getOrderNo());
        try {
            List<HzChinatelecomPreOrderReceiveRecord> list = new ArrayList<>();
            String effectTime = new SimpleDateFormat("yyyyMM").format(baseOrder.getTradeDate());
            int index = Arrays.binarySearch(accountPeriods, effectTime);
            BigDecimal totalAmount = baseOrder1.getTotalAmount().divide(new BigDecimal(baseOrder1.getDetail().getAuthStep()), 2, BigDecimal.ROUND_UP);
            HzChinatelecomPreOrderReceiveRecord record;
            for (int i = 0; i < baseOrder1.getDetail().getAuthStep(); i++) {
                record = new HzChinatelecomPreOrderReceiveRecord();
                record.setOrderNo(baseOrder1.getOrderNo());
                record.setAccountPeriod(accountPeriods[index++]);
                record.setPayMoney(totalAmount);
                record.setStep(i+1);
                record.setExpireTime(parseDate(record.getAccountPeriod()));
                //处理第一期转支付金额
                if (i == 0) {
                    JSONObject jsonObject = service.freezePay(totalAmount, baseOrder1.getOrderNo());
                    if(jsonObject!=null){
                        record.setFlowId(jsonObject.getLong("flowId"));
                        record.setSellerId(jsonObject.getString("sellerId"));
                    }
                }
                list.add(record);
            }
            HzChinatelecomPreOrderReceiveRecordRepository hzChinatelecomPreOrderReceiveRecordRepository = SpringUtil.getBean(HzChinatelecomPreOrderReceiveRecordRepository.class);
            hzChinatelecomPreOrderReceiveRecordRepository.saveAll(list);
        } catch (Exception e) {
            logger.error("杭州电信处理预授权首期转支付异常：", e);
        }
        logger.info("结束处理杭州电信预授权业务，订单号：{}", baseOrder1.getOrderNo());
    }
}