package com.mybank.service.impl;

import com.mybank.base.entity.AlipayPreAuthOrderDetail;
import com.mybank.base.entity.AlipayTradeOrderDetail;
import com.mybank.base.entity.BaseOrder;
import com.mybank.base.entity.constant.OrderStatus;
import com.mybank.base.entity.constant.OrderTradeType;
import com.mybank.base.repository.AlipayPreAuthOrderDetailRepository;
import com.mybank.base.repository.AlipayTradeOrderDetailRepository;
import com.mybank.base.repository.BaseOrderRepository;
import com.mybank.service.BaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/25
 */
@Service
public class BaseOrderServiceImpl extends BaseServiceImpl implements BaseOrderService {

	@Autowired
	private BaseOrderRepository baseOrderRepository;

	@Autowired
	private AlipayTradeOrderDetailRepository alipayTradeOrderDetailRepository;

	@Autowired
	private AlipayPreAuthOrderDetailRepository alipayPreAuthOrderDetailRepository;

	@Override
	public void saveOrder(BaseOrder baseOrder) {
		baseOrderRepository.save(baseOrder);
		Object detail = baseOrder.getDetail();
		if(detail != null) {
			if(detail instanceof AlipayTradeOrderDetail){
				alipayTradeOrderDetailRepository.save((AlipayTradeOrderDetail)detail);
			}else if(detail instanceof AlipayPreAuthOrderDetail){
				alipayPreAuthOrderDetailRepository.save((AlipayPreAuthOrderDetail)detail);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> BaseOrder<T> find(long orderNo) {
		return find(orderNo,true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> BaseOrder<T> find(long orderNo,boolean hasDetail) {
		BaseOrder baseOrder = baseOrderRepository.findById(orderNo).orElse(null);
		if(hasDetail) {
			if (baseOrder != null) {
				try{
					baseOrder.setDetail(findOrderDetail(baseOrder.getOrderNo(),baseOrder.getTradeType()));
				}catch (Exception ignored){

				}
			}
		}
		return baseOrder;
	}

	@Override
	@Transactional
	public int updateOrderStatus(long orderNo, OrderStatus status) {
		return baseOrderRepository.updateStatus(orderNo, status.getCode());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> BaseOrder<T> findByThirdOrderNoAndAppId(String thirdOrderNo, long appId){
		BaseOrder baseOrder = baseOrderRepository.findTopByAppIdAndThirdOrderNo(appId, thirdOrderNo);
		if(baseOrder != null){
			baseOrder.setDetail(findOrderDetail(baseOrder.getOrderNo(),baseOrder.getTradeType()));
		}
		return baseOrder;
	}

	private Object findOrderDetail(long orderNo, String tradeType){
		if (OrderTradeType.ALIPAY_UN_STAGE.getCode().equals(tradeType) || OrderTradeType.ALIPAY_STAGE.getCode().equals(tradeType)) {
			return alipayTradeOrderDetailRepository.findTopByOrderNo(orderNo);
		}else if(OrderTradeType.ALIPAY_PRE_AUTH.getCode().equals(tradeType)){
			return alipayPreAuthOrderDetailRepository.findTopByOrderNo(orderNo);
		}
		return null;
	}

}
