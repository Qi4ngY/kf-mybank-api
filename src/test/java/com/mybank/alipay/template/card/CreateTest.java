package com.mybank.alipay.template.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.FileItem;
import com.alipay.api.request.AlipayMarketingCardOpenRequest;
import com.alipay.api.request.AlipayMarketingCardTemplateCreateRequest;
import com.alipay.api.request.AlipayOfflineMaterialImageUploadRequest;
import com.alipay.api.response.AlipayMarketingCardOpenResponse;
import com.alipay.api.response.AlipayMarketingCardTemplateCreateResponse;
import com.alipay.api.response.AlipayOfflineMaterialImageUploadResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.alipay.AlipayXmlConfigs;
import com.mybank.alipay.marketing.card.CardTemplateCreateBizContent;
import com.mybank.alipay.marketing.card.ColumnInfo;
import com.mybank.alipay.marketing.card.TemplateStyleInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/17
 */
public class CreateTest {

	private AlipayClient alipayClient;

	private String logoImageId;

	private String sloganImgId;

	private String backgroundId;

	@Before
	public void uploadPic()throws Exception{
		alipayClient = AlipayXmlConfigs.getAlipayClientModel("huihua-al_1@huianrong.com").getAlipayClient();
		AlipayOfflineMaterialImageUploadRequest request = new AlipayOfflineMaterialImageUploadRequest();
		request.setImageType("jpg");
		request.setImageName("会员卡");
		FileItem ImageContent = new FileItem("C:\\Users\\gavin\\Desktop\\doc\\logo.png");
		request.setImageContent(ImageContent);
		AlipayOfflineMaterialImageUploadResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
			logoImageId = response.getImageId();
		} else {
			System.out.println("调用失败");
		}

		request = new AlipayOfflineMaterialImageUploadRequest();
		request.setImageType("jpg");
		request.setImageName("会员卡");
		ImageContent = new FileItem("C:\\Users\\gavin\\Desktop\\doc\\backgroundId.jpg");
		request.setImageContent(ImageContent);
		response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
			backgroundId = response.getImageId();
		} else {
			System.out.println("调用失败");
		}

		request = new AlipayOfflineMaterialImageUploadRequest();
		request.setImageType("jpg");
		request.setImageName("会员卡");
		ImageContent = new FileItem("C:\\Users\\gavin\\Desktop\\doc\\sloganImgId.jpg");
		request.setImageContent(ImageContent);
		response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
			sloganImgId = response.getImageId();
		} else {
			System.out.println("调用失败");
		}
	}


	@Test
	public void create()throws Exception{

		AlipayMarketingCardTemplateCreateRequest request = new AlipayMarketingCardTemplateCreateRequest();
		request.setBizContent("{" +
				"\"request_id\":\""+System.currentTimeMillis()+"\"," +
				"\"card_type\":\"OUT_MEMBER_CARD\"," +
				"\"biz_no_prefix\":\"prex\"," +
				"\"biz_no_suffix_len\":\"10\"," +
				"\"write_off_type\":\"qrcode\"," +
				"\"template_style_info\":{" +
				"\"card_show_name\":\"花呗联名卡\"," +
				"\"logo_id\":\""+logoImageId+"\"," +
				"\"color\":\"rgb(55,112,179)\"," +
				"\"background_id\":\""+backgroundId+"\"," +
				"\"bg_color\":\"rgb(55,112,179)\"," +
				"\"front_text_list_enable\":false," +
				"\"front_image_enable\":false," +
				"\"feature_descriptions\":[" +
				"\"使用花呗卡可享受免费分期\"" +
				"]," +
				"\"slogan\":\"会员权益享不停\"," +
				"\"slogan_img_id\":\""+sloganImgId+"\"," +
				"\"brand_name\":\"可乐\"" +
				"}," +
				"\"template_benefit_info\":[{" +
				"\"title\":\"消费即折扣\"," +
				"\"benefit_desc\":[" +
				"\"消费即折扣\"" +
				"]," +
				"\"start_date\":\"2017-07-18 15:17:23\"," +
				"\"end_date\":\"2020-07-34 12:12:12\"" +
				"}]," +
				"\"column_info_list\":[{" +
				"\"code\":\"BENEFIT_INFO\"," +
				"\"operate_type\":\"openWeb\"," +
				"\"title\":\"会员专享\"," +
				"\"value\":\"80\"," +
				"\"more_info\":{" +
				"\"title\":\"会员专享权益\"," +
				"\"url\":\"https://www.taobao.com\"," +
				"\"params\":\"{}\"," +
				"\"descs\":[" +
				"\"会员生日7折\"" +
				"]" +
				"}" +
				"}]," +
				"\"field_rule_list\":[{" +
				"\"field_name\":\"Balance\"," +
				"\"rule_name\":\"ASSIGN_FROM_REQUEST\"," +
				"\"rule_value\":\"Balance\"" +
				"}]," +
				"\"card_action_list\":[{" +
				"\"code\":\"TO_CLOCK_IN\"," +
				"\"text\":\"打卡\"," +
				"\"url\":\"https://www.taobao.com\"" +
				"}]," +
				"\"open_card_conf\":{" +
				"\"open_card_source_type\":\"ISV\"," +
				"\"source_app_id\":\"201609191111111\"," +
				"\"open_card_url\":\"https://www.taobao.com\"" +
				"}," +
//				"\"service_label_list\":[" +
//				"\"HUABEI_FUWU\"" +
//				"]," +
//				"\"shop_ids\":[" +
//				"\"2015122900077000000002409504\"" +
//				"]," +
//				"\"pub_channels\":[{" +
//				"\"pub_channel\":\"SHOP_DETAIL\"," +
//				"\"ext_info\":\"\\\"key\\\":\\\"value\\\"\"" +
//				"}]," +
				"\"card_level_conf\":[{" +
				"\"level\":\"VIP1\"," +
				"\"level_show_name\":\"黄金会员\"," +
				"\"level_icon\":\"1T8Pp00AT7eo9NoAJkMR3AAAACMAAQEC\"," +
				"\"level_desc\":\"黄金会员享受免费停车\"" +
				"}]," +
				"\"mdcode_notify_conf\":{" +
				"\"url\":\"https://www.taobao.com\"" +
				"}," +
				"\"card_spec_tag\":\"NONE\"" +
				"}");
		AlipayMarketingCardTemplateCreateResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
		//20180517000000000999689000300547
		System.out.println(response.getTemplateId());
	}

	@Test
	public void send()throws Exception{
		AlipayMarketingCardOpenRequest request = new AlipayMarketingCardOpenRequest();
		request.setBizContent("{" +
				"\"out_serial_no\":\""+System.currentTimeMillis()+"\"," +
				"\"card_template_id\":\"20180517000000000999689000300547\"," +
				"\"card_user_info\":{" +
				"\"user_uni_id\":\"2088512812178942\"," +
				"\"user_uni_id_type\":\"UID\"" +
				"}," +
				"\"card_ext_info\":{" +
				"\"external_card_no\":\"EXT0001\"," +
				"\"open_date\":\"2014-02-2021:20:46\"," +
				"\"valid_date\":\"2020-02-2021:20:46\"," +
				"\"level\":\"VIP1\"," +
				"\"point\":\"88\"," +
				"\"balance\":\"124.89\"" +
//				"\"mdcode_info\":{" +
//				"\"code_status\":\"SUCCESS\"," +
//				"\"code_value\":\"1KFCDY0002\"," +
//				"\"expire_time\":\"2017-06-0916:25:53\"," +
//				"\"time_stamp\":1496996459" +
//				"}," +
//				"\"front_text_list\":[{" +
//				"\"label\":\"专业\"," +
//				"\"value\":\"金融贸易\"" +
//				"}]," +
//				"\"front_image_id\":\"9fxnkgt0QFmqKAl5V2BqxQAAACMAAQED\"" +
				"}," +
				"\"member_ext_info\":{" +
				"\"name\":\"张燎\"," +
				"\"gende\":\"MALE\"," +
				"\"birth\":\"2016-06-27\"," +
				"\"cell\":\"13000000000\"" +
				"}" +
//				"\"open_card_channel\":\"20161534000000000008863\"," +
//				"\"open_card_channel_id\":\"2088123123123123\"" +
				"}");
		AlipayMarketingCardOpenResponse response = alipayClient.execute(request,"authusrBfe6fea2fc656445dbe199d57feb96B18");
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}

}
