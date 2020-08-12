package com.mybank.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.mybank.alipay.AlipayClientModel;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthFreeze;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthRefund;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthTradePay;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthUnfreeze;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthFreezeResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthPayResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthRefundResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthUnfreezeResponse;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.base.entity.*;
import com.mybank.base.entity.constant.AlipayPreAuthFundsFlowType;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.base.entity.constant.OrderTradeType;
import com.mybank.base.repository.AlipayPreAuthFundsFlowRepository;
import com.mybank.config.AmqpConfig;
import com.mybank.config.RedisUtils;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AlipayPreAuthService;
import com.mybank.thread.PreauthRotationQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/8
 */
@Service
public class AlipayPreAuthServiceImpl extends BaseOrderServiceImpl implements AlipayPreAuthService {

	private static Logger logger = LoggerFactory.getLogger(AlipayPreAuthServiceImpl.class);

	@Value("${kf_alipay_pre_auth_go_url}")
	private String goAuthUrlPre;

	@Value("${kf_alipay_pre_auth_callback_url}")
	private String callbackUrlPre;

	@Autowired
	private AmqpConfig amqpConfig;

	@Autowired
	private AlipayPreAuthFundsFlowRepository preAuthFundsFlowRepository;

	@Autowired
	private RedisUtils redisUtils;

	@Override
	@Transactional(rollbackFor = Exception.class)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthFreezeResponse> freeze(KfAlipayPreAuthFreeze dto) throws Exception {
		ResponseEntity<KfAlipayPreAuthFreezeResponse> entity;

		if ((entity = validateMerchant(dto.getMerchantCode(), null)) != null) {
			return entity;
		}

        long merchantCode = ((Merchant) ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId();
        if ((entity = validateMerchantAlipayParams(merchantCode)) != null) {
            return entity;
        }

        String uuid = UUID.randomUUID().toString();
		long orderNo = BaseOrder.nextOrderNo();

        MerchantAlipayParams params = ThreadLocalParams.getInstance().getThreadVar("MerchantAlipayParams");

		if(params == null || StringUtils.isBlank(params.getAlipayAppId())){
            throw new Exception("未获取到appid");
        }
        String  preBizAppId = params.getAlipayAppId();

        AlipayClientModel alipayClientModel = AlipayXmlConfigs.getAlipayClientModel(preBizAppId);

		String bizContent = "{" +
				"\"out_order_no\":\"" + orderNo + "\"," +
				"\"out_request_no\":\"" + orderNo + "\"," +
				"\"order_title\":\"" + dto.getTitle() + "\"," +
				"\"amount\":" + dto.getPrice() + "," +
				"\"payee_user_id\":\"" + alipayClientModel.getAlipayUserId() + "\"," +
				"\"pay_timeout\":\"4m\"," +
				"\"product_code\":\"PRE_AUTH\"," +
				"\"enable_pay_channels\":\"[{\\\"payChannelType\\\":\\\"PCREDIT_PAY\\\"},{\\\"payChannelType\\\":\\\"MONEY_FUND\\\"}]\"" +
				"  }";


		AlipayResponse response;
		if ("off_line".equals(dto.getFreezeType())) {
			AlipayFundAuthOrderVoucherCreateRequest request = new AlipayFundAuthOrderVoucherCreateRequest();
			request.setBizContent(bizContent);
			request.setNotifyUrl(callbackUrlPre + uuid);
			response = alipayClientModel.getAlipayClient().execute(request);
			logger.info("支付宝线下预授权向支付宝请求参数AlipayAppId：{},request：{}", preBizAppId, JSON.toJSONString(request));
		} else {
			AlipayFundAuthOrderAppFreezeRequest request = new AlipayFundAuthOrderAppFreezeRequest();
			request.setBizContent(bizContent);
			request.setNotifyUrl(callbackUrlPre + uuid);
			response = alipayClientModel.getAlipayClient().sdkExecute(request);
			logger.info("支付宝线上预授权向支付宝请求参数AlipayAppId：{},request：{}", preBizAppId, JSON.toJSONString(request));
		}

		if (response.isSuccess()) {

			BaseOrder<AlipayPreAuthOrderDetail> baseOrder = new BaseOrder<>();
			baseOrder.setOrderNo(orderNo);
			baseOrder.setAppId(ThreadLocalParams.getInstance().getApp().getAppId());
			baseOrder.setMerchantCode(((Merchant) ThreadLocalParams.getInstance().getThreadVar("Merchant")).getId());
			baseOrder.setStatus(0);
			baseOrder.setTotalAmount(dto.getPrice());
			baseOrder.setTradeDate(new Date());
			baseOrder.setTradeType(OrderTradeType.ALIPAY_PRE_AUTH.getCode());
			baseOrder.setStoreId(dto.getStoreId());
            Merchant merchant = ThreadLocalParams.getInstance().getThreadVar("Merchant");

            if(merchant != null) {
                baseOrder.setDeptCode(merchant.getDeptCode());
            }

			baseOrder.setOperatorId(dto.getOperatorId());
			baseOrder.setThirdNotifyUrl(dto.getThirdNotifyUrl());
			//第三方订单号为空默认为订单号
			baseOrder.setThirdOrderNo(StringUtils.isBlank(dto.getThirdOrderNo()) ? String.valueOf(baseOrder.getOrderNo()) : dto.getThirdOrderNo());
			AlipayPreAuthOrderDetail detail = new AlipayPreAuthOrderDetail();
			detail.setOrderNo(baseOrder.getOrderNo());
			detail.setTitle(dto.getTitle());
			detail.setAlipayAppId(alipayClientModel.getAppId());
			detail.setExtraParams(dto.getExtraParams());
			detail.setAuthStep(dto.getAuthStep());
			detail.setBiz(dto.getBiz());

			KfAlipayPreAuthFreezeResponse response1 = new KfAlipayPreAuthFreezeResponse();
			if (response instanceof AlipayFundAuthOrderVoucherCreateResponse) {
				detail.setPayUrl(((AlipayFundAuthOrderVoucherCreateResponse) response).getCodeValue());
				response1.setAuthUrl(goAuthUrlPre + uuid);
			} else {
				response1.setFreezeStr(response.getBody());
			}
			response1.setOrderNo(baseOrder.getOrderNo());

			baseOrder.setDetail(detail);
			saveOrder(baseOrder);

			redisUtils.set("kf:alipay:preauth:go:" + uuid, String.valueOf(baseOrder.getOrderNo()), 15, TimeUnit.MINUTES);
			redisUtils.set("kf:alipay:preauth:callback:" + uuid, String.valueOf(baseOrder.getOrderNo()), 5, TimeUnit.HOURS);

			entity = new ResponseEntity<>();
			entity.setDto(response1);
			if(!amqpConfig.convertAndSendPreauthMsg("{\"order_no\":\""+ orderNo +"\",\"alipay_app_id\":\"" + alipayClientModel.getAppId() + "\",\"cur_time\":5,\"max_time\":300}",5)) {
				ThreadPoolManager.scheduledExecutorService.schedule(new PreauthRotationQuery(alipayClientModel.getAppId(), orderNo, uuid), 10, TimeUnit.SECONDS);
			}
		} else {
			entity = new ResponseEntity<>("40000", response.getSubMsg());
		}
		return entity;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<KfAlipayPreAuthUnfreezeResponse> unfreeze(KfAlipayPreAuthUnfreeze dto, BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception {

		ResponseEntity<KfAlipayPreAuthUnfreezeResponse> responseEntity = new ResponseEntity<>();

		AlipayClient alipayClient = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId()).getAlipayClient();
		AlipayFundAuthOrderUnfreezeRequest request = new AlipayFundAuthOrderUnfreezeRequest();
		long flowId = AlipayPreAuthFundsFlow.nextFlowId();
		request.setBizContent("{" +
				"\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"," +
				"\"out_request_no\":\"" + flowId + "\"," +
				"\"amount\":" + String.format("%.2f", dto.getAmount().floatValue()) + "," +
				"\"remark\":\"" + baseOrder.getDetail().getTitle() + "解冻\"" +
				"  }");
		AlipayFundAuthOrderUnfreezeResponse response = alipayClient.execute(request);
		if (response.isSuccess()) {
			AlipayFundAuthOperationDetailQueryRequest request1 = new AlipayFundAuthOperationDetailQueryRequest();
			request1.setBizContent("{" +
					"\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"" +
					",\"operation_id\":\"" + baseOrder.getDetail().getAlipayOrderNo() + "\"" +
					" }");
			AlipayFundAuthOperationDetailQueryResponse response2 = alipayClient.execute(request1);
			AlipayPreAuthFundsFlow flow = new AlipayPreAuthFundsFlow();
			flow.setFlowId(flowId);
			flow.setType(AlipayPreAuthFundsFlowType.UNFREEZE.getCode());
			flow.setRestAmount(new BigDecimal(response2.getRestAmount()));
			flow.setAlipayOrderNo(response2.getOperationId());
			flow.setAmount(dto.getAmount());
			flow.setOrderNo(baseOrder.getOrderNo());
			preAuthFundsFlowRepository.save(flow);
			baseOrder.getDetail().setRestAmount(new BigDecimal(response2.getRestAmount()));
			baseOrder.setRefundDate(new Date());
			baseOrder.setStatus(OrderStatus.UNFREEZE.getCode());
			saveOrder(baseOrder);
			KfAlipayPreAuthUnfreezeResponse kfResponse = new KfAlipayPreAuthUnfreezeResponse();
			kfResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
			kfResponse.setFlowId(flow.getFlowId());
			kfResponse.setRestAmount(baseOrder.getDetail().getRestAmount());
			kfResponse.setUnfreezeDate(flow.getCreateTime());
			kfResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
			kfResponse.setTotalAmount(flow.getAmount());
			responseEntity.setDto(kfResponse);

		} else {
			responseEntity.setMsg(response.getMsg());
			responseEntity.setResponseCode("40000");
		}

		return responseEntity;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject freezePay(BigDecimal totalAmount, long orderNo) throws Exception {
		JSONObject returnJson=new JSONObject();
		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = find(orderNo, true);
		AlipayClientModel alipayClientModel = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId());
		AlipayTradePayRequest request = new AlipayTradePayRequest();
		long flowId = AlipayPreAuthFundsFlow.nextFlowId();
		request.setBizContent("{" +
				"\"out_trade_no\":\"" + flowId + "\"" +
				",\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"" +
				",\"buyer_id\":\"" + baseOrder.getDetail().getPayerUserId() + "\"" +
				",\"product_code\":\"PRE_AUTH\"" +
				",\"subject\":\"" + baseOrder.getDetail().getTitle() + "冻结转支付\"" +
				",\"seller_id\":\"" + alipayClientModel.getAlipayUserId() + "\"" +
				",\"auth_confirm_mode\":\"NOT_COMPLETE\"" +
				",\"total_amount\":" + String.format("%.2f", totalAmount) +
				"}");
		AlipayTradePayResponse response = alipayClientModel.getAlipayClient().execute(request);
		returnJson.put("tradeNo",response.getTradeNo());
		returnJson.put("sellerId",alipayClientModel.getAlipayUserId());

		if (response.isSuccess()) {
			AlipayFundAuthOperationDetailQueryRequest request1 = new AlipayFundAuthOperationDetailQueryRequest();
			request1.setBizContent("{" +
					"\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"" +
					",\"operation_id\":\"" + baseOrder.getDetail().getAlipayOrderNo() + "\"" +
					" }");
			AlipayFundAuthOperationDetailQueryResponse response2 = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId()).getAlipayClient().execute(request1);
			AlipayPreAuthFundsFlow flow = new AlipayPreAuthFundsFlow();
			flow.setFlowId(flowId);
			flow.setType(AlipayPreAuthFundsFlowType.PAY.getCode());
			flow.setRestAmount(new BigDecimal(response2.getRestAmount()));
			flow.setAlipayOrderNo(response.getTradeNo());
			flow.setAmount(totalAmount);
			flow.setOrderNo(baseOrder.getOrderNo());
			preAuthFundsFlowRepository.save(flow);
			baseOrder.getDetail().setRestAmount(new BigDecimal(response2.getRestAmount()));
			baseOrder.getDetail().setTotalPayAmount(baseOrder.getTotalAmount().subtract(new BigDecimal(response2.getRestAmount())));
			saveOrder(baseOrder);
		}
		return returnJson;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<KfAlipayPreAuthPayResponse> apiPay(KfAlipayPreAuthTradePay dto, BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception {
		ResponseEntity<KfAlipayPreAuthPayResponse> responseEntity = new ResponseEntity<>();
		AlipayClientModel alipayClientModel = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId());
		AlipayTradePayRequest request = new AlipayTradePayRequest();
		long flowId = AlipayPreAuthFundsFlow.nextFlowId();
		request.setBizContent("{" +
				"\"out_trade_no\":\"" + flowId + "\"" +
				",\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"" +
				",\"buyer_id\":\"" + baseOrder.getDetail().getPayerUserId() + "\"" +
				",\"product_code\":\"PRE_AUTH\"" +
				",\"subject\":\"" + baseOrder.getDetail().getTitle() + "冻结转支付\"" +
				",\"seller_id\":\"" + alipayClientModel.getAlipayUserId() + "\"" +
				",\"auth_confirm_mode\":\"NOT_COMPLETE\"" +
				",\"total_amount\":" + String.format("%.2f", dto.getTotalAmount()) + "" +
				"    }");
		AlipayTradePayResponse response = alipayClientModel.getAlipayClient().execute(request);
		if (response.isSuccess()) {
			AlipayFundAuthOperationDetailQueryRequest request1 = new AlipayFundAuthOperationDetailQueryRequest();
			request1.setBizContent("{" +
					"\"auth_no\":\"" + baseOrder.getDetail().getAuthNo() + "\"" +
					",\"operation_id\":\"" + baseOrder.getDetail().getAlipayOrderNo() + "\"" +
					" }");
			AlipayFundAuthOperationDetailQueryResponse response2 = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId()).getAlipayClient().execute(request1);
			AlipayPreAuthFundsFlow flow = new AlipayPreAuthFundsFlow();
			flow.setFlowId(flowId);
			flow.setType(AlipayPreAuthFundsFlowType.PAY.getCode());
			flow.setRestAmount(new BigDecimal(response2.getRestAmount()));
			flow.setAlipayOrderNo(response.getTradeNo());
			flow.setAmount(dto.getTotalAmount());
			flow.setOrderNo(baseOrder.getOrderNo());
			preAuthFundsFlowRepository.save(flow);
			baseOrder.getDetail().setRestAmount(new BigDecimal(response2.getRestAmount()));
			saveOrder(baseOrder);

			KfAlipayPreAuthPayResponse payResponse = new KfAlipayPreAuthPayResponse();
			payResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
			payResponse.setFlowId(flow.getFlowId());
			payResponse.setRestAmount(baseOrder.getDetail().getRestAmount());
			payResponse.setRefundDate(flow.getCreateTime());
			payResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
			payResponse.setTotalAmount(flow.getAmount());
			responseEntity.setDto(payResponse);

		} else {
			responseEntity.setMsg(response.getMsg());
			responseEntity.setResponseCode("40000");
		}
		return responseEntity;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<KfAlipayPreAuthRefundResponse> refund(KfAlipayPreAuthRefund dto, BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception {

		AlipayPreAuthFundsFlow flow = preAuthFundsFlowRepository.findById(dto.getFlowId()).orElse(null);
		if (flow == null || flow.getOrderNo() != dto.getOrderNo() || StringUtils.isBlank(flow.getAlipayOrderNo())
				|| flow.getAmount() == null) {
			return new ResponseEntity<>("40000", "异常订单！");
		}

		ResponseEntity<KfAlipayPreAuthRefundResponse> responseEntity = new ResponseEntity<>();
		//判断订单是否已经退款
		AlipayPreAuthFundsFlow temp = preAuthFundsFlowRepository.getByOrderNoAndAlipayOrderNoAndType(baseOrder.getOrderNo(),
				flow.getAlipayOrderNo(), AlipayPreAuthFundsFlowType.REFUND.getCode());
		if (temp != null) {//已退款
			KfAlipayPreAuthRefundResponse refundResponse = new KfAlipayPreAuthRefundResponse();
			refundResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
			refundResponse.setFlowId(temp.getFlowId());
			refundResponse.setRefundDate(temp.getCreateTime());
			refundResponse.setAlipayOrderNo(temp.getAlipayOrderNo());
			refundResponse.setTotalAmount(temp.getAmount());
			responseEntity.setDto(refundResponse);
		} else {
			AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
			request.setBizContent("{" +
					"\"out_trade_no\":\"" + dto.getFlowId() + "\"," +
					"\"remark\":\"" + baseOrder.getDetail().getTitle() + "\"," +
					"\"refund_amount\":" + String.format("%.2f", flow.getAmount().floatValue()) +
					" }");
			AlipayTradeRefundResponse response = AlipayXmlConfigs.getAlipayClientModel(baseOrder.getDetail().getAlipayAppId()).getAlipayClient().execute(request);
			if (response.isSuccess()) {
				AlipayPreAuthFundsFlow refundFlow = new AlipayPreAuthFundsFlow();
				refundFlow.setFlowId(AlipayPreAuthFundsFlow.nextFlowId());
				refundFlow.setType(AlipayPreAuthFundsFlowType.REFUND.getCode());
				refundFlow.setRestAmount(baseOrder.getDetail().getRestAmount());
				refundFlow.setAlipayOrderNo(response.getTradeNo());
				refundFlow.setAmount(flow.getAmount());
				refundFlow.setOrderNo(baseOrder.getOrderNo());
				preAuthFundsFlowRepository.save(refundFlow);
				KfAlipayPreAuthRefundResponse refundResponse = new KfAlipayPreAuthRefundResponse();
				refundResponse.setAlipayOrderNo(flow.getAlipayOrderNo());
				refundResponse.setFlowId(refundFlow.getFlowId());
				refundResponse.setRefundDate(refundFlow.getCreateTime());
				refundResponse.setAlipayOrderNo(refundFlow.getAlipayOrderNo());
				refundResponse.setTotalAmount(refundFlow.getAmount());
				refundResponse.setRestAmount(baseOrder.getDetail().getRestAmount());
				responseEntity.setDto(refundResponse);
			} else {
				responseEntity.setResponseCode("40000");
				responseEntity.setMsg(response.getSubMsg());
			}
		}
		return responseEntity;
	}

}
