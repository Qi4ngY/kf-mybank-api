package com.mybank.service;

import com.mybank.api.request.dto.trade.pay.*;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.trade.pay.*;
import com.mybank.base.entity.AlipayTradeOrderDetail;
import com.mybank.base.entity.BaseOrder;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/22
 */
public interface AlipayTradePayService extends BaseOrderService{


	@SuppressWarnings("unchecked")
	BaseOrder<AlipayTradeOrderDetail> findByThirdOrderNoAndAppId(String thirdOrderNo, long appId);

	/**
	 * 根据支付宝订单号查询订单
	 * @param alipayOrderNo 支付宝订单号
	 */
	BaseOrder<AlipayTradeOrderDetail> findByAlipayOrderNo(String alipayOrderNo);

	/**
	 * 支付宝订单交易
	 * @param kfAlipayTradePay 业务请求参数
	 */
	ResponseEntity<KfAlipayTradePayResponse> pay(KfAlipayTradePay kfAlipayTradePay);

	/**
	 * 线上app交易
	 * @param kfAlipayTradeApp 业务请求参数
	 */
	ResponseEntity<KfAlipayTradeAppResponse> app(KfAlipayTradeApp kfAlipayTradeApp);

	/**
	 * 线上H5交易
	 * @param kfAlipayTradeH5 业务请求参数
	 */
	ResponseEntity<KfAlipayTradeH5Response> h5(KfAlipayTradeH5 kfAlipayTradeH5);

	/**
	 * 支付宝订单预创建
	 * @param kfAlipayTradePay 业务请求参数
	 */
	ResponseEntity<KfAlipayTradePrecreateResponse> payPrecreate(KfAlipayTradePrecreate kfAlipayTradePay);

	/**
	 * 支付宝订单退款
	 * @param baseOrder 订单信息
	 */
	ResponseEntity<KfAlipayTradeRefundResponse> refund(BaseOrder<AlipayTradeOrderDetail> baseOrder, KfAlipayTradeRefund dto);

}
