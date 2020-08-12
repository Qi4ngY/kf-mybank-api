package com.mybank.service;

import com.mybank.base.entity.BaseOrder;
import com.mybank.base.entity.constant.OrderStatus;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/25
 */
public interface BaseOrderService extends BaseService {

	void saveOrder(BaseOrder baseOrder);

	/**
	 * 更新订单状态
	 * @param orderNo 订单号
	 * @param status 目标状态
	 * @return 更新数量
	 */
	int updateOrderStatus(long orderNo, OrderStatus status);

	/**
	 * 根据订单号查询订单详情
	 * @param orderNo 订单号
	 * @param <T> 订单对应的tradeType对应不同的详情
	 * @return 订单信息
	 */
	<T> BaseOrder<T> find(long orderNo);

	/**
	 * 根据订单号查询订单详情
	 * @param orderNo 订单号
	 * @param <T> 订单对应的tradeType对应不同的详情
	 * @param hasDetail 是否查询详情
	 * @return 订单信息
	 */
	<T> BaseOrder<T> find(long orderNo,boolean hasDetail);

	/**
	 * 根据第三方订单号和appId查询花呗分期订单
	 * @param thirdOrderNo 第三方订单号
	 * @param appId 公司分配的应用ID
	 */
	<T> BaseOrder<T> findByThirdOrderNoAndAppId(String thirdOrderNo, long appId);
}
