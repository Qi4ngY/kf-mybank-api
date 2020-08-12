package com.mybank.api.preauth;

import com.alibaba.fastjson.JSON;
import com.mybank.api.request.dto.RequestEntity;
import com.mybank.api.request.dto.preauth.*;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.perauth.*;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.base.entity.AlipayPreAuthFundsFlow;
import com.mybank.base.entity.AlipayPreAuthOrderDetail;
import com.mybank.base.entity.BaseOrder;
import com.mybank.base.entity.constant.OrderTradeType;
import com.mybank.base.repository.AlipayPreAuthFundsFlowRepository;
import com.mybank.base.repository.AlipayPreAuthOrderDetailRepository;
import com.mybank.config.RedisUtils;
import com.mybank.service.AlipayPreAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/8
 */
@RestController
@RequestMapping(value = "/api/alipay/preauth/")
public class AlipayPreAuthController {

	private static Logger logger = LoggerFactory.getLogger(AlipayPreAuthController.class);

	private static final String PER_AUTH_FREEZE_IDEMPOTENT_CONTROL_KEY = "kf:preauth:freeze:idempotent:";

	private static final String PER_AUTH_UNFREEZE_IDEMPOTENT_CONTROL_KEY = "kf:preauth:unfreeze:idempotent:";

	private static final String PER_AUTH_CONSUMER_IDEMPOTENT_CONTROL_KEY = "kf:preauth:funds:consume:idempotent:";

	private static final String PER_AUTH_REFUND_IDEMPOTENT_CONTROL_KEY = "kf:preauth:refund:idempotent:";

	@Autowired
	private AlipayPreAuthService orderService;

	@Autowired
	private AlipayPreAuthService alipayPreAuthService;

	@Autowired
	private AlipayPreAuthFundsFlowRepository preAuthFundsFlowRepository;

	@Autowired
	private AlipayPreAuthOrderDetailRepository alipayPreAuthOrderDetailRepository;

	@Autowired
	private RedisUtils redisUtils;

    private ResponseEntity preDeal(BaseOrder<AlipayPreAuthOrderDetail> baseOrder, BigDecimal operAmount) {

		if (baseOrder == null || baseOrder.getDetail() == null) {
			return new ResponseEntity<>("40000", "订单异常");
		}

		if (baseOrder.getAppId() != ThreadLocalParams.getInstance().getApp().getAppId()) {
			return new ResponseEntity<>("40000", "参数错误");
		}

		if (operAmount != null) {
			if (baseOrder.getDetail().getRestAmount().compareTo(operAmount) < 0) {
				return new ResponseEntity<>("40000", "金额大于冻结金额");
			}
		}

		return null;
	}

	@RequestMapping("/unfreeze")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_UNFREEZE)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthUnfreezeResponse> unfreeze(RequestEntity<KfAlipayPreAuthUnfreeze> entity) throws Exception {
		if (!redisUtils.setIfAbsent(PER_AUTH_UNFREEZE_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getOrderNo(),
				String.format("%.2f", entity.getDto().getAmount().floatValue()),
				5, TimeUnit.SECONDS)) {
			return new ResponseEntity<>("40000", "请求太频繁，请稍后再试...");
		}
		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = alipayPreAuthService.find(entity.getDto().getOrderNo());
		ResponseEntity<KfAlipayPreAuthUnfreezeResponse> responseEntity = preDeal(baseOrder, entity.getDto().getAmount());
		return responseEntity != null ? responseEntity : alipayPreAuthService.unfreeze(entity.getDto(), baseOrder);
	}

	@RequestMapping("/freezeQuery")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_QUERY)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthQueryResponse> freezeQuery(RequestEntity<KfAlipayPreAuthQuery> entity) {

		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = alipayPreAuthService.find(entity.getDto().getOrderNo());
		ResponseEntity<KfAlipayPreAuthQueryResponse> responseEntity = preDeal(baseOrder, null);
		if (responseEntity == null) {
			responseEntity = new ResponseEntity<>();
			KfAlipayPreAuthQueryResponse response = new KfAlipayPreAuthQueryResponse();
			response.setOrderNo(baseOrder.getOrderNo());
			response.setMerchantCode(baseOrder.getMerchantCode());
			response.setStatus(baseOrder.getStatus());
			response.setOperDate(baseOrder.getTradeDate());
			response.setStoreId(baseOrder.getStoreId());
			response.setOperatorId(baseOrder.getOperatorId());
			response.setAmount(baseOrder.getTotalAmount());
			response.setRestAmount(baseOrder.getDetail().getRestAmount());
			response.setAuthStep(baseOrder.getDetail().getAuthStep());
			response.setPayerLogonId(baseOrder.getDetail().getPayerLogonId());
			response.setPayerUserId(baseOrder.getDetail().getPayerUserId());
			response.setAuthTime(baseOrder.getDetail().getAuthTime());
			response.setAlipayOrderNo(baseOrder.getDetail().getAlipayOrderNo());
			response.setTitle(baseOrder.getDetail().getTitle());
			response.setRefundDate(baseOrder.getRefundDate());
			response.setBiz(baseOrder.getDetail().getBiz());
			response.setExtraParams(baseOrder.getDetail().getExtraParams());
			List<AlipayPreAuthFundsFlow> fundsFlowList = preAuthFundsFlowRepository.getByOrderNoOrderByFlowIdDesc(baseOrder.getOrderNo());
			fundsFlowList.forEach(v -> {
				KfAlipayPreAuthQueryResponse.OperationFlow operationFlow = new KfAlipayPreAuthQueryResponse.OperationFlow();
				operationFlow.setAlipayOrderNo(v.getAlipayOrderNo());
				operationFlow.setFlowId(v.getFlowId());
				operationFlow.setAmount(v.getAmount());
				operationFlow.setType(v.getType());
				operationFlow.setCreateTime(v.getCreateTime());
				operationFlow.setRestAmount(v.getRestAmount());
				response.getFlows().add(operationFlow);
			});
			responseEntity.setDto(response);
		}
		return responseEntity;
	}

	@RequestMapping("/freeze")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_FREEZE)
	public ResponseEntity<KfAlipayPreAuthFreezeResponse> freeze(RequestEntity<KfAlipayPreAuthFreeze> entity) throws Exception {
		if (!redisUtils.setIfAbsent(PER_AUTH_FREEZE_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getOperatorId(),
				String.valueOf(entity.getDto().getOperatorId()), 5, TimeUnit.SECONDS)) {
			return new ResponseEntity<>("40000", "请求太频繁，请稍后再试...");
		}
		return alipayPreAuthService.freeze(entity.getDto());
	}

	@RequestMapping("/queryList")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_QUERY_LIST)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthQueryListResponse> queryList(RequestEntity<KfAlipayPreAuthQueryList> entity) throws Exception {
		KfAlipayPreAuthQueryList dto = entity.getDto();
		com.mybank.base.Page page = com.mybank.base.Page.toPage(alipayPreAuthOrderDetailRepository.findPage(
                dto.getMerchantCode(),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dto.getStartDate() + " 00:00:00"),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dto.getEndDate() + " 23:59:59"),
				dto.getMerCode(), dto.getOperLogin(),
				dto.getStatus() == -1 ? null : dto.getStatus(),
				OrderTradeType.ALIPAY_PRE_AUTH.getCode(),
				dto.getBiz(),
				PageRequest.of(dto.getPageNo() - 1, dto.getPageSize())
		));
		ResponseEntity<KfAlipayPreAuthQueryListResponse> responseEntity = new ResponseEntity<>();
		KfAlipayPreAuthQueryListResponse response = new KfAlipayPreAuthQueryListResponse();
		response.setTotal(page.getTotal());
		response.setRows(page.getRows());
		responseEntity.setDto(response);
		return responseEntity;

	}

	@RequestMapping("/pay")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_TRADE_PAY)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthPayResponse> pay(RequestEntity<KfAlipayPreAuthTradePay> entity) throws Exception {

		if (!redisUtils.setIfAbsent(PER_AUTH_CONSUMER_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getOrderNo(),
				String.valueOf(entity.getDto().getOrderNo()),
				5, TimeUnit.SECONDS)) {
			return new ResponseEntity<>("40000", "请求太频繁，请稍后再试...");
		}

		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = orderService.find(entity.getDto().getOrderNo());
		ResponseEntity<KfAlipayPreAuthPayResponse> responseEntity = preDeal(baseOrder, entity.getDto().getTotalAmount());
		return responseEntity != null ? responseEntity : alipayPreAuthService.apiPay(entity.getDto(), baseOrder);
	}

	@RequestMapping("/refund")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_PRE_AUTH_TRADE_REFUND)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KfAlipayPreAuthRefundResponse> refund(RequestEntity<KfAlipayPreAuthRefund> entity) throws Exception {
		if (!redisUtils.setIfAbsent(PER_AUTH_REFUND_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getOrderNo(),
				String.valueOf(entity.getDto().getOrderNo()),
				5, TimeUnit.SECONDS)) {
			return new ResponseEntity<>("40000", "业务处理中...");
		}

		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = orderService.find(entity.getDto().getOrderNo());
		ResponseEntity<KfAlipayPreAuthRefundResponse> responseEntity = preDeal(baseOrder, null);
		return responseEntity != null ? responseEntity : alipayPreAuthService.refund(entity.getDto(), baseOrder);
	}

	@RequestMapping("/callback/{uuid}")
	public String callback(@PathVariable("uuid") String uuid, HttpServletRequest request) throws Exception {

		logger.info("支付宝回调uuid:{},参数:{}", uuid, JSON.toJSONString(request.getParameterMap()));
		redisUtils.convertAndSend("mybankDataSyn", "{\"order_no\":\"" + request.getParameter("out_order_no") + "\"}");
		return "success";
	}
}
