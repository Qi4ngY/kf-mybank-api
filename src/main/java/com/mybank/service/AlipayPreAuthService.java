package com.mybank.service;

import com.alibaba.fastjson.JSONObject;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthFreeze;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthTradePay;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthRefund;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthUnfreeze;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthFreezeResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthPayResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthRefundResponse;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthUnfreezeResponse;
import com.mybank.base.entity.AlipayPreAuthOrderDetail;
import com.mybank.base.entity.BaseOrder;

import java.math.BigDecimal;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/8
 */
public interface AlipayPreAuthService extends BaseOrderService{

	ResponseEntity<KfAlipayPreAuthFreezeResponse> freeze(KfAlipayPreAuthFreeze dto) throws Exception;

	ResponseEntity<KfAlipayPreAuthUnfreezeResponse> unfreeze(KfAlipayPreAuthUnfreeze dto,BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception;

	ResponseEntity<KfAlipayPreAuthPayResponse> apiPay(KfAlipayPreAuthTradePay dto, BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception;

	JSONObject freezePay(BigDecimal totalAmount, long orderNo) throws Exception;

	ResponseEntity<KfAlipayPreAuthRefundResponse> refund(KfAlipayPreAuthRefund dto,BaseOrder<AlipayPreAuthOrderDetail> baseOrder) throws Exception;

}
