package com.mybank.api.base;

import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipayMarketingFacetofaceDecodeUseResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.api.request.dto.RequestEntity;
import com.mybank.api.request.dto.base.AlipayInfo;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.base.AlipayInfoResponse;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.mybank.alipay.AlipayRequestClient.facetofaceDecodeUseResponse;

/**
 * 功能描述: 网商kf接口测试<br/>
 * 当面付付款码解码
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/7
 */
@RestController
@RequestMapping("/api/base/alipay")
public class AlipayInfoController {

	private static Logger logger = LoggerFactory.getLogger(AlipayInfoController.class);

	private static final String ALIPAY_AUTH_CODE_PARSE_PID = "kf:alipay:pid:";

	@Value(value = "${kf_default_alipay_app_id}")
	private String defaultAlipayAppId;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@PostMapping("/decodeAlipayAuthCode")
	@KfApi(method = KfApiMethodCode.KF_DECODE_ALIPAY_AUTH_CODE)
	public ResponseEntity<AlipayInfoResponse> decodeAuthCode(RequestEntity<AlipayInfo> entity)throws Exception{
		ResponseEntity<AlipayInfoResponse> entity1 = new ResponseEntity<>();
		String authCode = ThreadLocalParams.getInstance().getApp().getRsa().privateDecrypt(entity.getDto().getAuthCode());
		Boolean exists = redisTemplate.hasKey(ALIPAY_AUTH_CODE_PARSE_PID + authCode);
		if (exists != null && exists) {
			AlipayInfoResponse response1 = new AlipayInfoResponse();
			response1.setAlipayUserId(redisTemplate.opsForValue().get(ALIPAY_AUTH_CODE_PARSE_PID + authCode));
			entity1.setDto(response1);
		}else {
			AlipayMarketingFacetofaceDecodeUseResponse response = facetofaceDecodeUseResponse(AlipayXmlConfigs.getAlipayClientModel(defaultAlipayAppId), "{\"dynamic_id\":\"" +
					authCode + "\",\"sence_no\":\"" + System.currentTimeMillis() + "\"}");
			if (response.isSuccess() && StringUtils.isNotEmpty(response.getUserId())) {
				AlipayInfoResponse response1 = new AlipayInfoResponse();
				response1.setAlipayUserId(response.getUserId());
				entity1.setDto(response1);
				redisTemplate.opsForValue().set(ALIPAY_AUTH_CODE_PARSE_PID + authCode, response.getUserId(), 5L, TimeUnit.MINUTES);
			} else {
				entity1.setResponseCode("40003");
				entity1.setMsg(response.getSubMsg());
			}
		}
		return entity1;
	}

	//appId2017050507129426 huihua-al@huianrong.com
	@GetMapping("/authCallBack")
	public void authCallback(HttpServletRequest request, HttpServletResponse response)throws Exception{
		if(!MD5Util.encode("kf-mybank-api").equalsIgnoreCase(request.getParameter("state"))){
			response.getWriter().println("{\"error\":\"非法请求！\"}");
			return;
		}
		AlipaySystemOauthTokenRequest alipaySystemOauthTokenRequest = new AlipaySystemOauthTokenRequest();
		alipaySystemOauthTokenRequest.setCode(request.getParameter("auth_code"));
		alipaySystemOauthTokenRequest.setGrantType("authorization_code");
		AlipaySystemOauthTokenResponse oauthTokenResponse = AlipayXmlConfigs.getAlipayClientModel(request.getParameter("app_id")).getAlipayClient().execute(alipaySystemOauthTokenRequest);
		response.getWriter().println("{\"alipay_user_id\":\"" + oauthTokenResponse.getUserId()+"\"}");
	}


}
