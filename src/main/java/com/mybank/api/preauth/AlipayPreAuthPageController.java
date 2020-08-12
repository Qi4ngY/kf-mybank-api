package com.mybank.api.preauth;

import com.alibaba.fastjson.JSONObject;
import com.mybank.base.entity.AlipayPreAuthOrderDetail;
import com.mybank.base.entity.BaseOrder;
import com.mybank.config.RedisUtils;
import com.mybank.service.AlipayPreAuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/16
 */
@Controller
@RequestMapping(value = "/api/alipay/preauth/")
public class AlipayPreAuthPageController {

    private static Logger logger = LoggerFactory.getLogger(AlipayPreAuthPageController.class);

	@Autowired
	private AlipayPreAuthService orderService;

	@Value("${kf_alipay_pre_auth_callback_url}")
	private String callbackUrlPre;

	@Value("${kf_alipay_pre_auth_success}")
	private String authSuccessUrl;

	@Autowired
	private RedisUtils redisUtils;

	@RequestMapping("/sPage")
	public String sPage(){
		return "sPage";
	}

	@RequestMapping("/goAuth/{uuid}")
	public String goAuth(@PathVariable("uuid") String uuid, ModelMap model){

		String orderNoStr = redisUtils.get("kf:alipay:preauth:go:" + uuid);
		if(StringUtils.isBlank(orderNoStr)){
			return "failed";
		}

		BaseOrder<AlipayPreAuthOrderDetail> baseOrder = orderService.find(Long.parseLong(orderNoStr));
		if(baseOrder == null || baseOrder.getDetail() == null){
			return "error";
		}

		JSONObject extraParams = JSONObject.parseObject(baseOrder.getDetail().getExtraParams());
		model.addAllAttributes(extraParams);
		model.addAttribute("title",baseOrder.getDetail().getTitle());
		model.addAttribute("total_amount",String.format("%.2f",baseOrder.getTotalAmount().floatValue()));
		model.addAttribute("pay_url",baseOrder.getDetail().getPayUrl());
		model.addAttribute("uuid", uuid);
		model.addAttribute("biz", baseOrder.getDetail().getBiz());
		return "preAuthConfirm/"+baseOrder.getDetail().getBiz();
	}

}
