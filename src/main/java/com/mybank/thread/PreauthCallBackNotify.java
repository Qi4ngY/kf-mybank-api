package com.mybank.thread;

import com.mybank.base.entity.AlipayPreAuthOrderDetail;
import com.mybank.base.entity.App;
import com.mybank.base.entity.BaseOrder;
import com.mybank.config.SpringUtil;
import com.mybank.pool.ThreadPoolManager;
import com.mybank.service.AlipayPreAuthService;
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
 * @since 2019/8/9
 */
public class PreauthCallBackNotify implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(PreauthCallBackNotify.class);

	private long orderNo;

	private int index;

	private static int[] scheduledTimes = {0, 5, 10, 30, 60, 300};

	PreauthCallBackNotify(long orderNo, int index) {
		this.orderNo = orderNo;
		this.index = index;
	}

	@Override
	public void run() {

		AlipayPreAuthService orderService = SpringUtil.getBean(AlipayPreAuthService.class);
		BaseOrder<AlipayPreAuthOrderDetail> order = orderService.find(orderNo);
		if(order == null || StringUtils.isBlank(order.getThirdNotifyUrl())){
			return;
		}
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			Map<String, String> params = new TreeMap<>();

			params.put("order_no", String.valueOf(order.getOrderNo()));
			params.put("merchant_code", String.valueOf(order.getMerchantCode()));

			params.put("third_order_no", order.getThirdOrderNo());
			params.put("total_amount", String.format("%.2f", order.getTotalAmount().floatValue()));
			params.put("trade_date", order.getTradeDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getTradeDate()));
			if (order.getDetail() != null) {
				params.put("buyer_logon_id", order.getDetail().getPayerLogonId());
				params.put("buyer_user_id", order.getDetail().getPayerUserId());
//				params.put("auth_step", String.valueOf(order.getDetail().getAuthStep()));
				params.put("rest_amount", String.format("%.2f", order.getDetail().getRestAmount()));
				params.put("auth_no", order.getDetail().getAuthNo());
				params.put("trade_date", order.getDetail().getAuthTime() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getDetail().getAuthTime()));
				params.put("alipay_order_no", order.getDetail().getAlipayOrderNo());
			}
			params.put("refund_date", order.getRefundDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getRefundDate()));
			params.put("reason", order.getReason());
			params.put("status", String.valueOf(order.getStatus()));

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
					ThreadPoolManager.notifyExecutorService.schedule(new PreauthCallBackNotify(orderNo, index), scheduledTimes[index], TimeUnit.SECONDS);
				}
			} else {
				logger.info("订单{}推送成功！", order.getOrderNo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
