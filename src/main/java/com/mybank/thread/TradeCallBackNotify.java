package com.mybank.thread;

import com.mybank.base.entity.AlipayTradeOrderDetail;
import com.mybank.base.entity.App;
import com.mybank.base.entity.BaseOrder;
import com.mybank.config.SpringUtil;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AppService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/6/22
 */
public class TradeCallBackNotify implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(TradeCallBackNotify.class);

	private BaseOrder<AlipayTradeOrderDetail> order;

	private int index;

	private static int[] scheduledTimes = {0, 5, 10, 30, 60, 300};

	TradeCallBackNotify(BaseOrder<AlipayTradeOrderDetail> order, int index) {
		this.order = order;
		this.index = index;
	}

	@Override
	public void run() {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			Map<String, String> params = new TreeMap<>();

			params.put("orderNo", String.valueOf(order.getOrderNo()));
			params.put("merchantCode", String.valueOf(order.getMerchantCode()));

			params.put("thirdOrderNo", order.getThirdOrderNo());
			params.put("totalAmount", String.format("%.2f", order.getTotalAmount().floatValue()));
			params.put("tradeDate", order.getTradeDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getTradeDate()));
			if (order.getDetail() != null) {
				params.put("buyerLogonId", order.getDetail().getAlipayLogonId());
				params.put("buyerUserId", order.getDetail().getAlipayUserId());
				params.put("stageNum", order.getDetail().getStageNum());
				params.put("percent", order.getDetail().getPercent());
				params.put("subject", order.getDetail().getSubject());
				params.put("alipayOrderNo", order.getDetail().getAlipayOrderNo());
//					params.put("actualMoney",String.format("%.2f",order.getDetail().getActualMoney().floatValue()));
			}
			params.put("refundDate", order.getRefundDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getRefundDate()));
			params.put("reason", order.getReason());
			params.put("status", String.valueOf(order.getStatus()));

			if (StringUtils.isNotBlank(order.getOperatorId())) {
				params.put("operatorId", order.getOperatorId());
			}
			HttpPost post = new HttpPost(order.getThirdNotifyUrl());
			List<NameValuePair> urlParameters = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			params.forEach((k, v) -> {
				if (StringUtils.isNotEmpty(v)) {
					urlParameters.add(new BasicNameValuePair(k, v));
					if (!"".equals(sb.toString())) {
						sb.append("&");
					}
					sb.append(k).append("=").append(v);
				}
			});
			App app = SpringUtil.getBean(AppService.class).findByAppId(order.getAppId());
			String sign = app.getRsa().sign(sb.toString().trim());
			logger.info("签名字段：{}", sb.toString().trim());
			logger.info("签名：{}", sign);
			urlParameters.add(new BasicNameValuePair("sign", sign));
			HttpEntity postParams = new UrlEncodedFormEntity(urlParameters, Consts.UTF_8.name());
			post.setEntity(postParams);
			post.addHeader("User-Agent", "Mozilla/5.0");
			CloseableHttpResponse httpResponse = httpClient.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();
			httpClient.close();
			++index;
			if (!"success".equalsIgnoreCase(String.valueOf(response).trim())) {
				logger.info("订单{}第{}次推送失败！", order.getOrderNo(), index);
				if (index >= scheduledTimes.length) {
					logger.error("订单{}最终推送失败！", order.getOrderNo());
				} else {
					ThreadPoolManager.notifyExecutorService.schedule(new TradeCallBackNotify(order, index), scheduledTimes[index], TimeUnit.SECONDS);
				}
			} else {
				logger.info("订单{}推送成功！", order.getOrderNo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
