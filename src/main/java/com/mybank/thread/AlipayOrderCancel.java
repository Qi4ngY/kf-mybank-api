package com.mybank.thread;

import com.alipay.api.AlipayApiException;
import com.mybank.alipay.AlipayRequestClient;
import com.mybank.alipay.AlipayXmlConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能描述: 网商kf接口测试<br/>
 * 系统异常发起订单取消操作
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/27
 */
public class AlipayOrderCancel implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(AlipayOrderCancel.class);

	private String alipayAppId;

	private long orderNo;

	public AlipayOrderCancel(String alipayAppId, long orderNo) {
		this.alipayAppId = alipayAppId;
		this.orderNo = orderNo;
	}

	@Override
	public void run() {
		try{
			AlipayRequestClient.alipayTradeCancelRequest(
					AlipayXmlConfigs.getAlipayClientModel(alipayAppId),
					orderNo);
		}catch (AlipayApiException ignored){

		}

	}
}
