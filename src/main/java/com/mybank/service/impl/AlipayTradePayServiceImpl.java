package com.mybank.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.mybank.alipay.AlipayRequestClient;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.alipay.pay.*;
import com.mybank.api.request.dto.trade.common.GoodsDetail;
import com.mybank.api.request.dto.trade.pay.*;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.trade.pay.*;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.base.entity.*;
import com.mybank.base.entity.constant.AlipayStageCalModal;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.base.entity.constant.OrderTradeScene;
import com.mybank.base.entity.constant.OrderTradeType;
import com.mybank.base.repository.AlipayTradeGoodsDetailRepository;
import com.mybank.base.repository.AlipayTradeOrderDetailRepository;
import com.mybank.base.repository.BaseOrderRepository;
import com.mybank.base.repository.MerchantAlipayParamsRepository;
import com.mybank.config.AmqpConfig;
import com.mybank.exception.CommonException;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AlipayTradePayService;
import com.mybank.thread.AlipayOrderCancel;
import com.mybank.thread.TradeRotationQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/22
 */
@Service
public class AlipayTradePayServiceImpl extends BaseOrderServiceImpl implements AlipayTradePayService {

	private static final Logger logger = LoggerFactory.getLogger(AlipayTradePayServiceImpl.class);

	@Autowired
	private BaseOrderRepository orderRepository;

	@Autowired
	private AlipayTradeOrderDetailRepository detailRepository;

	@Autowired
	private MerchantAlipayParamsRepository paramsRepository;

	@Autowired
	private AlipayTradeGoodsDetailRepository alipayTradeGoodsDetailRepository;

	@Autowired
	private AmqpConfig amqpConfig;

	@SuppressWarnings("unchecked")
	@Override
	public BaseOrder<AlipayTradeOrderDetail> findByAlipayOrderNo(String alipayOrderNo) {
		AlipayTradeOrderDetail detail = detailRepository.findTopByAlipayOrderNo(alipayOrderNo);
		if (detail != null) {
			BaseOrder<AlipayTradeOrderDetail> order = orderRepository.getOne(detail.getOrderNo());
			order.setDetail(detail);
			return order;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseOrder<AlipayTradeOrderDetail> findByThirdOrderNoAndAppId(String thirdOrderNo, long appId) {
		BaseOrder<AlipayTradeOrderDetail> order = orderRepository.findTopByAppIdAndThirdOrderNo(appId, thirdOrderNo);
		if (order != null) {
			AlipayTradeOrderDetail detail = detailRepository.getOne(order.getOrderNo());
			order.setDetail(detail);
		}
		return order;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<KfAlipayTradePayResponse> pay(KfAlipayTradePay kfAlipayTradePay) {

		ResponseEntity<KfAlipayTradePayResponse> response;
		if ((response = validateMerchant(kfAlipayTradePay.getMerchantCode(), kfAlipayTradePay.getThirdMerchantId())) != null) {
			return response;
		}
		long merchantCode = ((Merchant) ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId();
		if ((response = validateMerchantAlipayParams(merchantCode)) != null) {
			return response;
		}

		if ((response = validateAlipayStageTradeParams(kfAlipayTradePay)) != null) {
			return response;
		}

		AlipayTradePay content = createAlipayTradePay(kfAlipayTradePay);
		long orderNo = BaseOrder.nextOrderNo();
		String alipayAppId = ((MerchantAlipayParams) ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams")).getAlipayAppId();
		try {
			content.setOutTradeNo(String.valueOf(orderNo));
			AlipayTradePayResponse alipayResponse = AlipayRequestClient.alipayTradePayRequest(
					AlipayXmlConfigs.getAlipayClientModel(alipayAppId),
					content);
			if (alipayResponse.isSuccess()) {
				BaseOrder<AlipayTradeOrderDetail> payOrder = createOrderAndSave(kfAlipayTradePay, alipayResponse, content, OrderTradeScene.OFF_LINE.getCode());
				response = new ResponseEntity<>();
				KfAlipayTradePayResponse dto = new KfAlipayTradePayResponse();
				dto.setAlipayOrderNo(payOrder.getDetail().getAlipayOrderNo());
				dto.setBuyerLogonId(payOrder.getDetail().getAlipayLogonId());
				dto.setBuyerUserId(payOrder.getDetail().getAlipayUserId());
				dto.setActualMoney(payOrder.getDetail().getActualMoney());
				dto.setOrderNo(payOrder.getOrderNo());
				dto.setMerchantCode(payOrder.getMerchantCode());
				dto.setThirdOrderNo(payOrder.getThirdOrderNo());
				dto.setTradeDate(payOrder.getTradeDate());
				dto.setTotalAmount(payOrder.getTotalAmount());
				dto.setStageNum(payOrder.getDetail().getStageNum());
				dto.setPercent(payOrder.getDetail().getPercent());
				dto.setStatus(payOrder.getStatus());
				response.setDto(dto);
				//支付成功后定时向支付宝发送查询请求，保证订单状态的一致性
				scheduledQueryAlipayOrder(alipayAppId, orderNo);
			} else {
				response = new ResponseEntity();
				response.setResponseCode("40003");
				response.setMsg(alipayResponse.getSubMsg());
			}
		} catch (Exception e) {
			logger.error("支付宝请求异常：", e);
			ThreadPoolManager.executorService.execute(new AlipayOrderCancel(alipayAppId, orderNo));
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	private List<com.mybank.alipay.pay.GoodsDetail> createGoodsDetails(GoodsDetail[] goodsDetail) {
		if(goodsDetail != null){
			List<com.mybank.alipay.pay.GoodsDetail> goodsDetails = new ArrayList<>();
			com.mybank.alipay.pay.GoodsDetail payGoods;
			for (GoodsDetail detail : goodsDetail) {
				payGoods = new com.mybank.alipay.pay.GoodsDetail();
				payGoods.setGoodsId(detail.getGoodsId());
				payGoods.setGoodsName(detail.getGoodsName());
				payGoods.setQuantity(detail.getQuantity());
				payGoods.setPrice(String.valueOf(detail.getPrice()));
				goodsDetails.add(payGoods);
			}
			return goodsDetails;
		}
		return null;
	}

	/**
	 * 保存商品明细
	 * @param orderNo 订单号
	 * @param goodsDetails 商品信息
	 */
	private void saveGoods(long orderNo, List<com.mybank.alipay.pay.GoodsDetail> goodsDetails) {
		if(goodsDetails == null || goodsDetails.size() == 0){
			return;
		}
		AlipayTradeGoodsDetail goods;
		List<AlipayTradeGoodsDetail> list = new ArrayList<>();
		for (com.mybank.alipay.pay.GoodsDetail detail : goodsDetails) {
			goods = new AlipayTradeGoodsDetail();
			goods.setGoodsId(detail.getGoodsId());
			goods.setGoodsName(detail.getGoodsName());
			goods.setOrderNo(orderNo);
			goods.setPrice(new BigDecimal(detail.getPrice()));
			goods.setQuantity(detail.getQuantity());
			list.add(goods);
		}
		alipayTradeGoodsDetailRepository.saveAll(list);
	}

	/**
	 * 支付宝订单预创建
	 *
	 * @param kfAlipayTradePrecreate 业务请求参数
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<KfAlipayTradePrecreateResponse> payPrecreate(KfAlipayTradePrecreate kfAlipayTradePrecreate) {
		ResponseEntity<KfAlipayTradePrecreateResponse> response;
		if ((response = validateMerchant(kfAlipayTradePrecreate.getMerchantCode(), kfAlipayTradePrecreate.getThirdMerchantId())) != null) {
			return response;
		}
		long merchantCode = ((Merchant) ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId();
		if ((response = validateMerchantAlipayParams(merchantCode)) != null) {
			return response;
		}

		if ((response = validateAlipayStageTradeParams(kfAlipayTradePrecreate)) != null) {
			return response;
		}
		AlipayTradePrecreate bizContent = createAlipayTradePrecreate(kfAlipayTradePrecreate);
		long orderNo = BaseOrder.nextOrderNo();
		String alipayAppId = ((MerchantAlipayParams) ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams")).getAlipayAppId();
		try {
			bizContent.setOutTradeNo(String.valueOf(orderNo));
			AlipayTradePrecreateResponse alipayResponse = AlipayRequestClient.alipayTradePrecreateRequest(
					AlipayXmlConfigs.getAlipayClientModel(alipayAppId),
					bizContent);
			if (alipayResponse.isSuccess()) {
				BaseOrder<AlipayTradeOrderDetail> payOrder = createOrderAndSave(kfAlipayTradePrecreate, alipayResponse, bizContent, OrderTradeScene.OFF_LINE.getCode());
				response = new ResponseEntity<>();
				KfAlipayTradePrecreateResponse dto = new KfAlipayTradePrecreateResponse();
				dto.setOrderNo(payOrder.getOrderNo());
				dto.setMerchantCode(payOrder.getMerchantCode());
				dto.setThirdOrderNo(payOrder.getThirdOrderNo());
				dto.setTradeDate(payOrder.getTradeDate());
				dto.setTotalAmount(payOrder.getTotalAmount());
				dto.setStageNum(payOrder.getDetail().getStageNum());
				dto.setPercent(payOrder.getDetail().getPercent());
				dto.setPayUrl(alipayResponse.getQrCode());
				response.setDto(dto);
				//支付成功后定时向支付宝发送查询请求，保证订单状态的一致性
				scheduledQueryAlipayOrder(alipayAppId, orderNo);
			} else {
				response = new ResponseEntity();
				response.setResponseCode("40003");
				response.setMsg(alipayResponse.getSubMsg());
			}
		} catch (Exception e) {
			logger.error("支付宝订单预创建请求异常：", e);
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	private BaseOrder<AlipayTradeOrderDetail> createOrderAndSave(KfAlipayTradePrecreate kfAlipayTradePay, AlipayResponse response, AlipayTradePrecreate content, String tradeScene) {
		BaseOrder<AlipayTradeOrderDetail> order = new BaseOrder<>();

		if (response instanceof AlipayTradePayResponse) {
			order.setOrderNo(Long.parseLong(((AlipayTradePayResponse) response).getOutTradeNo()));
		} else if (response instanceof AlipayTradePrecreateResponse) {
			order.setOrderNo(Long.parseLong(((AlipayTradePrecreateResponse) response).getOutTradeNo()));
		}

		long merchantCode = ((Merchant) ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId();
		order.setMerchantCode(merchantCode);
		order.setTotalAmount(kfAlipayTradePay.getPrice());
		order.setTradeDate(ThreadLocalParams.getInstance().getCurrentDate());
		if (StringUtils.isBlank(kfAlipayTradePay.getStageNum())) {
			order.setTradeType(OrderTradeType.ALIPAY_UN_STAGE.toString());
		} else {
			order.setTradeType(OrderTradeType.ALIPAY_STAGE.toString());
		}
		order.setStatus(OrderStatus.PAYING.getCode());
		order.setThirdOrderNo(kfAlipayTradePay.getThirdOrderNo());
		order.setAppId(ThreadLocalParams.getInstance().getApp().getAppId());
		order.setThirdNotifyUrl(kfAlipayTradePay.getThirdNotifyUrl());
		//绝对不为空！ 请求时就校验了 商户
		Merchant merchant = ThreadLocalParams.getInstance().getThreadVar("Merchant");
        order.setDeptCode(merchant.getDeptCode());

		order.setStoreId(kfAlipayTradePay.getStoreId());
		order.setOperatorId(kfAlipayTradePay.getOperatorId());
		AlipayTradeOrderDetail detail = new AlipayTradeOrderDetail();
		if (response instanceof AlipayTradePayResponse) {
			detail.setAlipayOrderNo(StringUtils.defaultString(((AlipayTradePayResponse) response).getTradeNo(), ""));
			detail.setActualMoney(new BigDecimal(StringUtils.defaultString(((AlipayTradePayResponse) response).getTotalAmount(), "0.00")));
			detail.setAlipayLogonId(StringUtils.defaultString(((AlipayTradePayResponse) response).getBuyerLogonId(), ""));
			detail.setAlipayUserId(StringUtils.defaultString(((AlipayTradePayResponse) response).getBuyerUserId(), ""));
		} else if (response instanceof AlipayTradePrecreateResponse) {
			detail.setPayUrl(StringUtils.defaultString(((AlipayTradePrecreateResponse) response).getQrCode(), ""));
			detail.setActualMoney(new BigDecimal(content.getTotalAmount()));
		}
		MerchantAlipayParams params = ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams");
		//分期交易设置费率，分期数以及手续费承担方
		if (StringUtils.isNotBlank(kfAlipayTradePay.getStageNum())) {
			detail.setFee(params.fee(kfAlipayTradePay.getPercent(), kfAlipayTradePay.getStageNum()));
			detail.setPercent(kfAlipayTradePay.getPercent());
			detail.setStageNum(kfAlipayTradePay.getStageNum());
			App app = ThreadLocalParams.getInstance().getApp();
			//计算应用所属商户返佣金额以及费率
//			long merchantCode = ((Merchant)ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId();
			if (app.getMerchantCode() != 0 && merchantCode != app.getMerchantCode()) {
				MerchantAlipayParams perMerchantParams = paramsRepository.findById(app.getMerchantCode()).orElse(null);
				if (perMerchantParams != null) {
					String perMerchantFee = perMerchantParams.fee(kfAlipayTradePay.getPercent(), kfAlipayTradePay.getStageNum());
					if (StringUtils.isNotBlank(perMerchantFee)) {
						detail.setPerMerchantFee(perMerchantFee);
						BigDecimal perMerchantMoney = kfAlipayTradePay.getPrice().multiply(new BigDecimal(detail.getFee()).subtract(new BigDecimal(perMerchantFee)));
						detail.setPerMerchantMoney(perMerchantMoney.intValue());
					}
				}
			}
			//汇花收银台同步过来的商户计算一级代理商分佣
            if(app.getMerchantCode() == 0 && app.getAppId().equals(125247135296790528L)){
                //todo :等待所有交易都移植到kf平台后要写一级代理商分佣金额
            }
		}
		//保存公司分佣金额
		try {
		    if(kfAlipayTradePay.getStageCalModal() == AlipayStageCalModal.DEFINE.getCode()){
                detail.setHarMoney(kfAlipayTradePay.getRoyaltyMoney().multiply(new BigDecimal(100)).intValue());
            }else {
                detail.setHarMoney(new BigDecimal(content.getRoyaltyInfo().getRoyaltyDetailInfos().get(0).getAmount()).multiply(new BigDecimal(100)).intValue());
            }
		} catch (Exception e) {
			detail.setHarMoney(0);
		}

		detail.setOrderNo(order.getOrderNo());
		detail.setAlipayAppId(params.getAlipayAppId());
		detail.setSubject(kfAlipayTradePay.getSubject());
		detail.setApiMethod(ThreadLocalParams.getInstance().getApiMethod());
		detail.setTradeScene(tradeScene);
		detail.setExtraParams(kfAlipayTradePay.getExtraParams());
		detail.setBiz(kfAlipayTradePay.getBiz());
		order.setDetail(detail);
		saveOrder(order);

		//保存商品明细
		saveGoods(order.getOrderNo(),content.getGoodsDetail());
		return order;
	}

	/**
	 * 判断分期费率
	 *
	 * @param kfAlipayTradePay 请求参数
	 */
	private ResponseEntity validateAlipayStageTradeParams(KfAlipayTradePrecreate kfAlipayTradePay) {

		ResponseEntity entity = null;
		if (StringUtils.isNotBlank(kfAlipayTradePay.getStageNum())) {
			MerchantAlipayParams params = ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams");
			if (params.fee(kfAlipayTradePay.getPercent(), kfAlipayTradePay.getStageNum()) == null) {
				entity = new ResponseEntity();
				entity.setResponseCode("40001");
				entity.setMsg("商户支付宝信息和费率没有配置！");
			}
		}
		return entity;
	}

	private AlipayTradePrecreate createAlipayTradePrecreate(KfAlipayTradePrecreate kfAlipayTradePrecreate) {
		AlipayTradePrecreate bizContent = new AlipayTradePrecreate();
		bizContent.setSubject(kfAlipayTradePrecreate.getSubject());
		bizContent.setBody(kfAlipayTradePrecreate.getSubject());
		MerchantAlipayParams params = ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams");
        if(StringUtils.isNotBlank(params.getAlipayUserId()) && !"mybank".equals(params.getAlipayUserId())) {
            bizContent.setSellerId(params.getAlipayUserId());
            bizContent.setStoreId(kfAlipayTradePrecreate.getStoreId());
        }
		bizContent.setTotalAmount(calTotalAmount(kfAlipayTradePrecreate, params));
		bizContent.setExtendParams(createExtendParams(kfAlipayTradePrecreate, params, bizContent));

		//请求交易参数增加商品明细

		bizContent.setGoodsDetail(createGoodsDetails(kfAlipayTradePrecreate.getGoodsDetail()));
		return bizContent;
	}

	private ExtendParams createExtendParams(KfAlipayTradePrecreate kfAlipayTradePrecreate, MerchantAlipayParams params, AlipayTradePrecreate bizContent) {
		//支付宝分润的参数统一在调用支付宝接口时配置
		ExtendParams extendParams = new ExtendParams();
		if (StringUtils.isNotBlank(kfAlipayTradePrecreate.getStageNum())) {//分期交易要计算分佣
			extendParams.setHbFqNum(kfAlipayTradePrecreate.getStageNum());
			if (kfAlipayTradePrecreate.getStageCalModal() == AlipayStageCalModal.OLD_MODAL.getCode()) {
				extendParams.setHbFqSellerPercent("100");
				String royaltyMoney = calRoyaltyMoney(kfAlipayTradePrecreate, params, bizContent.getTotalAmount());
				RoyaltyDetailInfo detailInfo = new RoyaltyDetailInfo();
				detailInfo.setAmount(royaltyMoney);
				detailInfo.setTransOut(params.getAlipayUserId());
                detailInfo.setDesc(kfAlipayTradePrecreate.getBiz());
				RoyaltyInfo royaltyInfo = new RoyaltyInfo();
				royaltyInfo.getRoyaltyDetailInfos().add(detailInfo);
				bizContent.setRoyaltyInfo(royaltyInfo);
			} else if (kfAlipayTradePrecreate.getStageCalModal() == AlipayStageCalModal.NEW_MODAL.getCode()) {
				extendParams.setHbFqSellerPercent("0");
				String royaltyMoney = calRoyaltyMoney(kfAlipayTradePrecreate, params, bizContent.getTotalAmount());
				RoyaltyDetailInfo detailInfo = new RoyaltyDetailInfo();
				detailInfo.setAmount(royaltyMoney);
				detailInfo.setTransOut(params.getAlipayUserId());
                detailInfo.setDesc(kfAlipayTradePrecreate.getBiz());
				RoyaltyInfo royaltyInfo = new RoyaltyInfo();
				royaltyInfo.getRoyaltyDetailInfos().add(detailInfo);
				bizContent.setRoyaltyInfo(royaltyInfo);
			} else if (kfAlipayTradePrecreate.getStageCalModal() == AlipayStageCalModal.DEFINE.getCode()) {
                extendParams.setHbFqSellerPercent(kfAlipayTradePrecreate.getPercent());
                String royaltyMoney = calRoyaltyMoney(kfAlipayTradePrecreate, params, bizContent.getTotalAmount());
                RoyaltyDetailInfo detailInfo = new RoyaltyDetailInfo();
                detailInfo.setAmount(royaltyMoney);
                detailInfo.setTransOut(params.getAlipayUserId());
                detailInfo.setDesc(kfAlipayTradePrecreate.getBiz());
                RoyaltyInfo royaltyInfo = new RoyaltyInfo();
                royaltyInfo.getRoyaltyDetailInfos().add(detailInfo);
                bizContent.setRoyaltyInfo(royaltyInfo);
            }
		}else {
			//非分期交易
			String merFeeScanPay = params.getFeeScanPay();
			if(StringUtils.isNotBlank(merFeeScanPay)){
				float orderTotal =  Float.parseFloat(bizContent.getTotalAmount());
				float merFee =  Float.parseFloat(merFeeScanPay);
				float aliScanPayFee =  Float.parseFloat(AlipayXmlConfigs.getConfig().getFeeScanPay());
				float royaltyMoney = (orderTotal* merFee)/100f  - Math.round(orderTotal * aliScanPayFee)/100f;
				if(royaltyMoney > 0.00f) {
					RoyaltyDetailInfo detailInfo = new RoyaltyDetailInfo();
					detailInfo.setAmount(String.format("%.2f", royaltyMoney));
					detailInfo.setTransOut(params.getAlipayUserId());
					detailInfo.setDesc("当面付分佣【"+kfAlipayTradePrecreate.getBiz()+"】");
					RoyaltyInfo royaltyInfo = new RoyaltyInfo();
					royaltyInfo.getRoyaltyDetailInfos().add(detailInfo);
					bizContent.setRoyaltyInfo(royaltyInfo);
				}
			}
		}
		return extendParams;
	}

	private AlipayTradePay createAlipayTradePay(KfAlipayTradePay kfAlipayTradePay) {

		AlipayTradePay bizContent = new AlipayTradePay();
		//传递的参数可能会导致中途截取而发生交易风险
		bizContent.setAuthCode(ThreadLocalParams.getInstance().getApp().getRsa().privateDecrypt(kfAlipayTradePay.getAuthCode()));
		bizContent.setSubject(kfAlipayTradePay.getSubject());
		bizContent.setBody(kfAlipayTradePay.getSubject());
		bizContent.setTimeoutExpress(kfAlipayTradePay.getTimeoutExpress());
		MerchantAlipayParams params = ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams");

		if(StringUtils.isNotBlank(params.getAlipayUserId()) && !"mybank".equals(params.getAlipayUserId())) {
            bizContent.setSellerId(params.getAlipayUserId());
            bizContent.setStoreId(kfAlipayTradePay.getStoreId());
        }

		bizContent.setTotalAmount(calTotalAmount(kfAlipayTradePay, params));
		bizContent.setExtendParams(createExtendParams(kfAlipayTradePay, params, bizContent));

		//请求交易参数增加商品明细

		bizContent.setGoodsDetail(createGoodsDetails(kfAlipayTradePay.getGoodsDetail()));
		return bizContent;
	}

	/**
	 * 计算公司分佣金额
	 */
	private String calRoyaltyMoney(KfAlipayTradePrecreate kfAlipayTrade, MerchantAlipayParams params, String totalAmount) {

		if (StringUtils.isBlank(kfAlipayTrade.getStageNum())) {
			return null;
		}

		//自定义 直接返回参数的值
		if(kfAlipayTrade.getStageCalModal() == AlipayStageCalModal.DEFINE.getCode()){
            return String.format("%.2f", kfAlipayTrade.getRoyaltyMoney().floatValue());
        }

        JSONObject costRate = AlipayXmlConfigs.getCostRate();

		float alipayFee = costRate.getJSONObject("100").getFloat(kfAlipayTrade.getStageNum());

		float price = kfAlipayTrade.getPrice().floatValue();

		float total = Float.parseFloat(totalAmount);

		float royaltyMoney = 0.0F;
		if ("0".equals(kfAlipayTrade.getPercent())) {
			if (kfAlipayTrade.getStageCalModal() == AlipayStageCalModal.OLD_MODAL.getCode()) {
				//用户承担手续费 用户支付金额 = 商品金额 * (1 + 公司给商户配置的费率)
				//计算公式 分佣金额 = 用户支付金额 - 支付宝手续费【我司成本费率】 - 商品金额
				royaltyMoney = (total * 100 - Math.round(total * alipayFee) - price * 100) / 100;
			}else if(kfAlipayTrade.getStageCalModal() == AlipayStageCalModal.NEW_MODAL.getCode()) {//新的分佣计算模式
				alipayFee = costRate.getJSONObject("0").getFloat(kfAlipayTrade.getStageNum());
				String merchantFeeStr = params.fee(kfAlipayTrade.getPercent(), kfAlipayTrade.getStageNum());
				float merchantFee = Float.parseFloat(merchantFeeStr);
				royaltyMoney = price * (1 + merchantFee / 100) / (1 + alipayFee / 100) - price;
			}
		} else {
			//商户承担手续费 用户支付金额 = 商品金额
			//计算方式 分佣金额 = 用户支付金额 * (公司给商户配置的费率 - 支付宝手续费【我司成本费率】)
			String merchantFeeStr = params.fee(kfAlipayTrade.getPercent(), kfAlipayTrade.getStageNum());
			float merchantFee = Float.parseFloat(merchantFeeStr);
			royaltyMoney = total  - Math.round(total*alipayFee) / 100f - Math.round(total * (100f-merchantFee))/100f;
		}
		if (royaltyMoney < 0) {
			throw new CommonException("商户费率费率出错！");
		}
		return String.format("%.2f", royaltyMoney);
	}

	/**
	 * 计算用户实际支付金额
	 */
	private String calTotalAmount(KfAlipayTradePrecreate kfAlipayTradePay, MerchantAlipayParams params) {

		//当面付直接返回价格 或分期模式为自定义
		if (StringUtils.isBlank(kfAlipayTradePay.getStageNum()) || kfAlipayTradePay.getStageCalModal() == AlipayStageCalModal.DEFINE.getCode()) {
			return String.format("%.2f", kfAlipayTradePay.getPrice().floatValue());
		}


		String merchantFeeStr = params.fee(kfAlipayTradePay.getPercent(), kfAlipayTradePay.getStageNum());
		if (merchantFeeStr == null) {
			throw new CommonException("商户费率配置错误!");
		}

		float merchantFee = Float.parseFloat(merchantFeeStr) / 100;

		float totalAmount;

		if ("0".equals(kfAlipayTradePay.getPercent())) {
			//用户承担手续费支付宝推荐模式
			if (kfAlipayTradePay.getStageCalModal() == 0) {
				totalAmount = Math.round(kfAlipayTradePay.getPrice().floatValue() * 100 * (1 + merchantFee)) / 100f;
			} else {//新模式
				float alipayFee = AlipayXmlConfigs.getCostRate().getJSONObject("0").getFloat(kfAlipayTradePay.getStageNum()) / 100f;
				totalAmount = kfAlipayTradePay.getPrice().floatValue() * (1 + merchantFee) / (1 + alipayFee);
			}
		} else {
			totalAmount = kfAlipayTradePay.getPrice().floatValue();
		}
		return String.format("%.2f", totalAmount);
	}

	/**
	 * 支付成功后定时向支付宝发送查询请求，保证订单状态的一致性
	 */
	private void scheduledQueryAlipayOrder(String alipayAppId, long orderNo) {
		//MQ消息发送失败继续走本地轮询
		if(!amqpConfig.convertAndSendPayMsg("{\"order_no\":\""+orderNo+"\",\"alipay_app_id\":\""+alipayAppId+"\",\"cur_time\":5,\"max_time\":360}",5)){
			ThreadPoolManager.scheduledExecutorService.schedule(new TradeRotationQuery(alipayAppId, orderNo), 2, TimeUnit.SECONDS);
		}
	}

	@Override
	@Transactional(rollbackFor = CommonException.class)
	public ResponseEntity<KfAlipayTradeRefundResponse> refund(BaseOrder<AlipayTradeOrderDetail> order, KfAlipayTradeRefund rdto) {
		JSONObject bizContent = new JSONObject();
		bizContent.put("out_trade_no", order.getOrderNo());
		bizContent.put("refund_amount", order.getDetail().getActualMoney());
		try {
			int result = orderRepository.refund(order.getOrderNo(), OrderStatus.REFUND.getCode(), rdto.getReason(),
					ThreadLocalParams.getInstance().getCurrentDate(), order.getVersion());
			if (result != 1) {
				throw new CommonException("修改订单状态失败，请稍后再试！");
			}
			AlipayTradeRefundResponse response = AlipayRequestClient.alipayTradeRefundRequest(
					AlipayXmlConfigs.getAlipayClientModel(order.getDetail().getAlipayAppId()),
					bizContent.toJSONString()
			);
			if (response.isSuccess()) {
				ResponseEntity<KfAlipayTradeRefundResponse> responseEntity = new ResponseEntity<>();
				KfAlipayTradeRefundResponse dto = new KfAlipayTradeRefundResponse();

				dto.setOrderNo(order.getOrderNo());
				dto.setMerchantCode(order.getMerchantCode());
				dto.setThirdOrderNo(order.getThirdOrderNo());
				dto.setTradeDate(order.getTradeDate());
				dto.setTotalAmount(order.getTotalAmount());
				if (order.getDetail() != null) {
					dto.setStageNum(order.getDetail().getStageNum());
					dto.setPercent(order.getDetail().getPercent());
					dto.setActualMoney(order.getDetail().getActualMoney());
					dto.setAlipayOrderNo(order.getDetail().getAlipayOrderNo());
					dto.setBuyerLogonId(order.getDetail().getAlipayLogonId());
					dto.setBuyerUserId(order.getDetail().getAlipayUserId());
				}
				dto.setStatus(OrderStatus.REFUND.getCode());
				dto.setRefundDate(new Date());
				dto.setReason(rdto.getReason());
				responseEntity.setDto(dto);
				return responseEntity;
			} else {
				throw new CommonException(response.getSubMsg());
			}
		} catch (Exception e) {
			logger.error("支付宝订单退款异常！", e);
			if (TimeoutException.class.equals(e.getClass())) {
				//todo:此处递归有待验证
				return refund(order, rdto);
			} else {
				throw new CommonException(e.getMessage());
			}
		}
	}

	@Override
	public ResponseEntity<KfAlipayTradeAppResponse> app(KfAlipayTradeApp kfAlipayTradeApp) {
		return null;
	}

	@Override
	public ResponseEntity<KfAlipayTradeH5Response> h5(KfAlipayTradeH5 kfAlipayTradeH5) {
		return null;
	}
}
