package com.mybank.api.trade;

import com.mybank.api.request.dto.RequestEntity;
import com.mybank.api.request.dto.trade.pay.*;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.trade.pay.*;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.base.Page;
import com.mybank.base.entity.*;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.config.RedisUtils;
import com.mybank.secret.XRsa;
import com.mybank.service.AlipayTradePayService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/17
 */
@RestController
@RequestMapping(value = "/api/alipay/trade/")
public class AlipayTradePayController extends AlipayTrade{

	private static final String TRADE_IDEMPOTENT_CONTROL_KEY = "kf:trade:idempotent:";

	private static final String REFUND_IDEMPOTENT_CONTROL_KEY = "kf:refund:idempotent:";

	@Autowired
	private AlipayTradePayService service;

	@Autowired
	private RedisUtils redisUtils;

	@PostMapping("/precreate")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_PRECREATE)
	public ResponseEntity<KfAlipayTradePrecreateResponse> payPrecreate(RequestEntity<KfAlipayTradePrecreate> entity){
		ResponseEntity<KfAlipayTradePrecreateResponse> response;
		if (!redisUtils.setIfAbsent(TRADE_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getThirdOrderNo(),
                "1", 5, TimeUnit.SECONDS)) {
			response = new ResponseEntity<>();
			response.setMsg("业务处理中，如果没有返回接口请调用查询接口查询！");
			response.setResponseCode("40000");
		} else {
			BaseOrder<AlipayTradeOrderDetail> order = service.findByThirdOrderNoAndAppId(entity.getDto().getThirdOrderNo(), entity.getAppId());
			if (order == null) {
				response = service.payPrecreate(entity.getDto());
			} else {
				response = new ResponseEntity<>();
				KfAlipayTradePrecreateResponse dto = new KfAlipayTradePrecreateResponse();
				dto.setOrderNo(order.getOrderNo());
				dto.setMerchantCode(order.getMerchantCode());
				dto.setThirdOrderNo(order.getThirdOrderNo());
				dto.setTradeDate(order.getTradeDate());
				dto.setTotalAmount(order.getTotalAmount());
				dto.setStageNum(order.getDetail().getStageNum());
				dto.setPercent(order.getDetail().getPercent());
				dto.setPayUrl(order.getDetail().getPayUrl());
				response.setDto(dto);
			}
		}
		return response;
	}

	@PostMapping("/pay")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_PAY)
	public ResponseEntity<KfAlipayTradePayResponse> pay(RequestEntity<KfAlipayTradePay> entity){
		ResponseEntity<KfAlipayTradePayResponse> response;
		if (!redisUtils.setIfAbsent(TRADE_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getThirdOrderNo(),
                "1", 5, TimeUnit.SECONDS)) {
			response = new ResponseEntity<>();
			response.setMsg("业务处理中，如果没有返回接口请调用查询接口查询！");
			response.setResponseCode("40000");
		} else {
			BaseOrder<AlipayTradeOrderDetail> order = service.findByThirdOrderNoAndAppId(entity.getDto().getThirdOrderNo(), entity.getAppId());
			if (order == null) {
				response = service.pay(entity.getDto());
			} else {
				response = new ResponseEntity<>();
				KfAlipayTradePayResponse dto = new KfAlipayTradePayResponse();
				dto.setAlipayOrderNo(order.getDetail().getAlipayOrderNo());
				dto.setBuyerLogonId(order.getDetail().getAlipayLogonId());
				dto.setBuyerUserId(order.getDetail().getAlipayUserId());
				dto.setOrderNo(order.getOrderNo());
				dto.setMerchantCode(order.getMerchantCode());
				dto.setThirdOrderNo(order.getThirdOrderNo());
				dto.setTradeDate(order.getTradeDate());
				dto.setTotalAmount(order.getTotalAmount());
				dto.setStageNum(order.getDetail().getStageNum());
				dto.setPercent(order.getDetail().getPercent());
				dto.setStatus(order.getStatus());
				response.setDto(dto);
			}
		}
		return response;
	}

	@PostMapping("/app")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_APP)
	public ResponseEntity<KfAlipayTradeAppResponse> app(RequestEntity<KfAlipayTradeApp> entity){
		ResponseEntity<KfAlipayTradeAppResponse> response;
		if (!redisUtils.setIfAbsent(TRADE_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getThirdOrderNo(),
				"1", 5, TimeUnit.SECONDS)) {
			response = new ResponseEntity<>();
			response.setMsg("业务处理中，如果没有返回接口请调用查询接口查询！");
			response.setResponseCode("40000");
		} else {
			BaseOrder<AlipayTradeOrderDetail> order = service.findByThirdOrderNoAndAppId(entity.getDto().getThirdOrderNo(), entity.getAppId());
			if (order == null) {
				response = service.app(entity.getDto());
			} else {
				response = new ResponseEntity<>();
				KfAlipayTradeAppResponse dto = new KfAlipayTradeAppResponse();

				dto.setOrderNo(order.getOrderNo());
				dto.setMerchantCode(order.getMerchantCode());
				dto.setThirdOrderNo(order.getThirdOrderNo());
				dto.setTradeDate(order.getTradeDate());
				dto.setTotalAmount(order.getTotalAmount());
				dto.setStageNum(order.getDetail().getStageNum());
				dto.setPercent(order.getDetail().getPercent());
				response.setDto(dto);
			}
		}
		return response;
	}

	@PostMapping("/query")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_QUERY)
	public ResponseEntity<KfAlipayTradeQueryResponse> query(RequestEntity<KfAlipayTradeQuery> entity){

		BaseOrder<AlipayTradeOrderDetail> order = getBaseOrder(entity.getAppId(),entity.getDto().getOrderNo(),
				entity.getDto().getThirdOrderNo(),entity.getDto().getAlipayOrderNo());

		ResponseEntity<KfAlipayTradeQueryResponse> responseEntity = new ResponseEntity<>();
		if(order == null || !entity.getAppId().equals(order.getAppId())){//订单存在，同时订单要属于请求的appId
			responseEntity.setResponseCode("40000");
			responseEntity.setMsg("交易不存在！");
		}else{
			KfAlipayTradeQueryResponse dto = new KfAlipayTradeQueryResponse();
			dto.setActualMoney(order.getDetail().getActualMoney());
			dto.setAlipayOrderNo(order.getDetail().getAlipayOrderNo());
			dto.setBuyerLogonId(order.getDetail().getAlipayLogonId());
			dto.setBuyerUserId(order.getDetail().getAlipayUserId());
			dto.setOrderNo(order.getOrderNo());
			dto.setMerchantCode(order.getMerchantCode());
			dto.setThirdOrderNo(order.getThirdOrderNo());
			dto.setTradeDate(order.getTradeDate());
			dto.setStatus(order.getStatus());
			dto.setTotalAmount(order.getTotalAmount());
			dto.setStageNum(order.getDetail().getStageNum());
			dto.setPercent(order.getDetail().getPercent());
			dto.setExtraParams(order.getDetail().getExtraParams());

			dto.setRefundDate(order.getRefundDate());
			dto.setReason(order.getReason());
			dto.setStoreId(order.getStoreId());
            dto.setOperatorId(order.getOperatorId());
			responseEntity.setDto(dto);
		}
		return responseEntity;
	}

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * @param entity{
     *
     * }
     * @return {
     *     order_no:
     *     status:
     *     refund_date:
     *     trade_date:
     *     total_amount:
     *     third_order_no:
     *     subject:
     *     stage_num:
     *     percent:
     *     alipay_logon_id:
     *     alipay_user_id:
     *     alipay_order_no:
     *     extra_params:
     * }
     * @throws Exception
     */
    @PostMapping("/queryList")
    @KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_QUERY_LIST)
    public ResponseEntity<List<Map<String,Object>>> queryList(RequestEntity<KfAlipayTradeQueryList> entity) throws Exception{

        KfAlipayTradeQueryList dto = entity.getDto();

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(QBaseOrder.baseOrder.appId.eq(entity.getAppId()));
        if(dto.getStatus() != null && dto.getStatus() != -1){
            predicateList.add(QBaseOrder.baseOrder.status.eq(dto.getStatus()));
        }

        if(StringUtils.isNotBlank(dto.getBiz())){
            predicateList.add(QAlipayTradeOrderDetail.alipayTradeOrderDetail.biz.stringValue().eq(dto.getBiz()));
        }

        if(StringUtils.isNotBlank(dto.getStartDate())){
            predicateList.add(QBaseOrder.baseOrder.tradeDate.goe(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dto.getStartDate() + " 00:00:00")));
        }

        if(StringUtils.isNotBlank(dto.getEndDate())){
            predicateList.add(QBaseOrder.baseOrder.tradeDate.loe(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dto.getEndDate() + " 23:59:59")));
        }

        if(StringUtils.isNotBlank(dto.getOperateId())){
            predicateList.add(QBaseOrder.baseOrder.operatorId.stringValue().eq(dto.getOperateId()));
        }

        Expression[] expressions = {QBaseOrder.baseOrder.orderNo.as("order_no"),
                QBaseOrder.baseOrder.status.as("status"),
                QBaseOrder.baseOrder.refundDate.as("refund_date"),
                QBaseOrder.baseOrder.tradeDate.as("trade_date"),
                QBaseOrder.baseOrder.totalAmount.as("total_amount"),
                QBaseOrder.baseOrder.thirdOrderNo.as("third_order_no"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.extraParams.as("extra_params"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.subject.as("subject"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.stageNum.as("stage_num"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.percent.as("percent"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.alipayLogonId.as("alipay_logon_id"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.alipayUserId.as("alipay_user_id"),
                QAlipayTradeOrderDetail.alipayTradeOrderDetail.alipayOrderNo.as("alipay_order_no")
                };
        JPAQuery<Tuple> jpaQuery = queryFactory.select(expressions)
                .from(QAlipayTradeOrderDetail.alipayTradeOrderDetail)
                .leftJoin(QBaseOrder.baseOrder)
                .on(QBaseOrder.baseOrder.orderNo.eq(QAlipayTradeOrderDetail.alipayTradeOrderDetail.orderNo))
				.orderBy(QBaseOrder.baseOrder.tradeDate.desc());
        jpaQuery.offset((dto.getPageNo() -1) * dto.getPageSize()).limit(dto.getPageSize());

        ResponseEntity<List<Map<String,Object>>> responseEntity = new ResponseEntity<>();
        responseEntity.setDto(Page.toList(jpaQuery, predicateList, expressions));
        return responseEntity;
    }

	private BaseOrder<AlipayTradeOrderDetail> getBaseOrder(long appId,String encodedOrderNo,String thirdOrderNo,String alipayOrderNo){
		BaseOrder<AlipayTradeOrderDetail> order;
		XRsa rsa = ThreadLocalParams.getInstance().getApp().getRsa();
		if(StringUtils.isNotEmpty(encodedOrderNo)){
			order = service.find(Long.parseLong(rsa.privateDecrypt(encodedOrderNo)));
		}else if(StringUtils.isNotEmpty(thirdOrderNo)){
			order = service.findByThirdOrderNoAndAppId(rsa.privateDecrypt(thirdOrderNo),appId);
		}else{
			order = service.findByAlipayOrderNo(rsa.privateDecrypt(alipayOrderNo));
		}
		return order;
	}

	@PostMapping("/refund")
	@KfApi(method = KfApiMethodCode.KF_ALIPAY_TRADE_REFUND)
	public ResponseEntity<KfAlipayTradeRefundResponse> refund(RequestEntity<KfAlipayTradeRefund> entity) {

		BaseOrder<AlipayTradeOrderDetail> order = getBaseOrder(entity.getAppId(),entity.getDto().getOrderNo(),
				entity.getDto().getThirdOrderNo(),entity.getDto().getAlipayOrderNo());

		ResponseEntity<KfAlipayTradeRefundResponse> responseEntity = new ResponseEntity<>();
		if (!redisUtils.setIfAbsent(REFUND_IDEMPOTENT_CONTROL_KEY + entity.getAppId() + entity.getDto().getThirdOrderNo(),
                "1", 5, TimeUnit.SECONDS)) {
			responseEntity = new ResponseEntity<>();
			responseEntity.setMsg("业务处理中，如果没有返回接口请调用查询接口查询！");
			responseEntity.setResponseCode("40000");
			return responseEntity;
		}
		if(order != null && entity.getAppId().equals(order.getAppId())){//订单存在，同时订单要属于请求的appId
			if(order.getStatus()== OrderStatus.REFUND.getCode()){
				KfAlipayTradeRefundResponse dto = new KfAlipayTradeRefundResponse();
//				dto.setActualMoney(order.getDetail().getActualMoney());
				dto.setAlipayOrderNo(order.getDetail().getAlipayOrderNo());
				dto.setBuyerLogonId(order.getDetail().getAlipayLogonId());
				dto.setBuyerUserId(order.getDetail().getAlipayUserId());
				dto.setOrderNo(order.getOrderNo());
				dto.setMerchantCode(order.getMerchantCode());
				dto.setThirdOrderNo(order.getThirdOrderNo());
				dto.setTradeDate(order.getTradeDate());
				dto.setTotalAmount(order.getTotalAmount());
				dto.setStageNum(order.getDetail().getStageNum());
				dto.setPercent(order.getDetail().getPercent());
				dto.setRefundDate(order.getRefundDate());
				dto.setReason(order.getReason());
				responseEntity.setDto(dto);
			}else {
				return service.refund(order,entity.getDto());
			}
		}else{
			responseEntity.setMsg("交易不存在！");
			responseEntity.setResponseCode("40001");
		}
		return responseEntity;
	}



}
