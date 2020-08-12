package com.mybank.alipay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybank.alipay.admission.BizCard;
import com.mybank.alipay.admission.BusinessAddress;
import com.mybank.alipay.admission.ContactInfo;
import com.mybank.alipay.admission.ZftCreateBizContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AlipayRequestClientTest {
	@Test
	public void zftCreateResponse() throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(format.parse("2018-06-01").getTime());

		ZftCreateBizContent bizContent = new ZftCreateBizContent();
		bizContent.setExternalId(String.valueOf(System.currentTimeMillis()));
		bizContent.setOutBizNo(String.valueOf(System.currentTimeMillis()));
		bizContent.setName("张燎");
		bizContent.setAliasName("燎哥");
		bizContent.setMerchantType("01");
		bizContent.setMcc("");
		bizContent.setCertNo("");
		bizContent.setCertType("201");
		bizContent.setLegalName("张燎");
		bizContent.setLegalCertNo("513002198510032013");
		bizContent.setServicePhone("15982393775");
		bizContent.setService(new String[]{""});

		BusinessAddress address = new BusinessAddress();

		address.setType("BUSINESS_ADDRESS");
		address.setProvinceCode("");
		address.setCityCode("");
		address.setDistrictCode("");
		address.setAddress("");

		bizContent.setBusinessAddress(address);

		List<ContactInfo> contactInfos = new ArrayList<>();
		ContactInfo info = new ContactInfo();
		info.setName("张燎");
//		info.setPhone("15982393775");
		info.setMobile("17340071225");
		info.setEmail("zliao520@gmail.com");
		info.setTag(new String[]{"02"});
		info.setType("LEGAL_PERSON");
		info.setIdCardNo("123456");
		contactInfos.add(info);

		bizContent.setContactInfos(contactInfos);
		List<BizCard> bizCards = new ArrayList<>();
		BizCard card = new BizCard();
		card.setAccountInstName("");
		card.setAccountNo("");
		card.setAccountInstProvince("");
		card.setAccountInstCity("");
		card.setAccountBranchName("");
		card.setUsageType("");
		card.setAccountType("");
		card.setAccountInstName("");
		card.setAccountInstId("");

		bizCards.add(card);
		bizContent.setBizCards(bizCards);

//		System.out.println(AlipayRequestClient.zftCreateResponse(AlipayXmlConfigs.getAlipayClientModel("jrkj6@huianrong.com"),bizContent));


	}

	@Test
	public void cardActivateUrlApply() throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("template_id","20180522000000001008885000300549");
		jsonObject.put("out_string","1111111");
		jsonObject.put("callback","https://www.taobao.com");
//		jsonObject.put("follow_app_id","20180517000000000999689000300547");//生活号appId
//
//		callback
//
		System.out.println(AlipayRequestClient.cardActivateUrlApply(AlipayXmlConfigs.getAlipayClientModel("huihua-al_1@huianrong.com"),jsonObject).getApplyCardUrl());
	}

	@Test
	public void cardFormTemplateSet() throws Exception{
		JSONObject jsonObject;
//		jsonObject.put("template_id","20180517000000000999689000300547");
//		jsonObject.put("out_string","gavin_test");
//		jsonObject.put("callback","http://118.24.155.142:8080/index.jsp");
		JSONObject fields = new JSONObject();
//		String[] fs = {
//		"OPEN_FORM_FIELD_MOBILE",//手机号
//		"OPEN_FORM_FIELD_GENDER",//性别
//		"OPEN_FORM_FIELD_NAME",//姓名
//		"OPEN_FORM_FIELD_BIRTHDAY",//生日（不含年份，如：01-01）
//		"OPEN_FORM_FIELD_BIRTHDAY_WITH_YEAR",//生日（含年份，如：1988-01-01）
//		"OPEN_FORM_FIELD_IDCARD",//身份证
//		"OPEN_FORM_FIELD_CERT_TYPE",//证件类型
//		"OPEN_FORM_FIELD_CERT_NO",//证件号
//		"OPEN_FORM_FIELD_EMAIL",//邮箱
//		"OPEN_FORM_FIELD_ADDRESS",//地址
//		"OPEN_FORM_FIELD_CITY",//城市
//		"OPEN_FORM_FIELD_IS_STUDENT",//是否学生认证
//		"OPEN_FORM_FIELD_MEMBER_GRADE"};//会员等级
//		20180517000000000999689000300547	20180521000000001010861000300542
		String biz = "{" +
				"\"template_id\":\"20180522000000001008885000300549\"," +
				"\"fields\":{" +
				"\"required\":\"{\\\"common_fields\\\":[\\\"OPEN_FORM_FIELD_NAME\\\"]}\"," +
				"\"optional\":\"{\\\"common_fields\\\":[\\\"OPEN_FORM_FIELD_GENDER\\\",\\\"OPEN_FORM_FIELD_CERT_TYPE\\\",\\\"OPEN_FORM_FIELD_CERT_NO\\\"]}\"" +
				"}" +
				"}";

//		jsonObject.put("follow_app_id","20180517000000000999689000300547");//生活号appId
//
//		callback
//
		System.out.println(AlipayRequestClient.cardFormTemplateSet(AlipayXmlConfigs.getAlipayClientModel("huihua-al_1@huianrong.com"),JSONObject.parseObject(biz)));
	}

	@Test
	public void alipayTradePrecreateResponse() throws Exception{
		String content = "{" +
				"\"out_trade_no\":\""+System.currentTimeMillis()+"\"," +
				"\"seller_id\":\"2088102146225135\"," +
				"\"total_amount\":88.88," +
				"\"discountable_amount\":8.88," +
				"\"subject\":\"Iphone616G\"," +
				"\"goods_detail\":[{" +
				"\"goods_id\":\"apple-01\"," +
				"\"goods_name\":\"ipad\"," +
				"\"quantity\":1," +
				"\"price\":2000," +
				"\"goods_category\":\"34543238\"," +
				"\"body\":\"特价手机\"," +
				"\"show_url\":\"http://www.alipay.com/xxx.jpg\"" +
				"}]," +
				"\"body\":\"Iphone616G\"," +
				"\"operator_id\":\"yx_001\"," +
				"\"store_id\":\"NJ_001\"," +
//				"\"disable_pay_channels\":\"pcredit,moneyFund,debitCardExpress\"," +
				"\"terminal_id\":\"NJ_T_001\"," +
				"\"extend_params\":{" +
				"\"sys_service_provider_id\":\"2088821153023555\"," +
				"\"hb_fq_num\":\"3\"," +
				"\"hb_fq_seller_percent\":\"100\"" +
				"}," +
				"\"timeout_express\":\"90m\"," +
				"\"royalty_info\":{" +
				"\"royalty_type\":\"ROYALTY\"," +
				"\"royalty_detail_infos\":[{" +
				"\"serial_no\":1," +
				"\"trans_in_type\":\"userId\"," +
				"\"batch_no\":\"123\"," +
				"\"out_relation_id\":\"20131124001\"," +
				"\"trans_out_type\":\"userId\"," +
				"\"trans_out\":\"2088101126765726\"," +
				"\"trans_in\":\"2088101126708402\"," +
				"\"amount\":0.1," +
				"\"desc\":\"分账测试1\"," +
				"\"amount_percentage\":\"100\"" +
				"}]" +
				"}," +
				"\"ext_user_info\":{" +
				"\"name\":\"李明\"," +
				"\"mobile\":\"16587658765\"," +
				"\"cert_type\":\"IDENTITY_CARD\"," +
				"\"cert_no\":\"362334768769238881\"," +
				"\"min_age\":\"18\"," +
				"\"fix_buyer\":\"F\"," +
				"\"need_check_info\":\"F\"" +
				"}," +
				"\"business_params\":\"{\\\"data\\\":\\\"123\\\"}\"" +
				"}";
	}

	@Test
	public void alipayFundAuthOrderVoucherCreateRequest() throws Exception {
		System.out.println(AlipayRequestClient.alipayFundAuthOrderVoucherCreateRequest(AlipayXmlConfigs.getAlipayClientModel("2018031602385788")).getCodeUrl());
	}

}