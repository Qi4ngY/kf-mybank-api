package com.mybank.alipay.retail;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.mybank.alipay.QiMenApiClient;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/8
 */
public class QiMenApiClientTest {

	private AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
			"2018032302433722",
			"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
			"json",
			"UTF-8",
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjHMY+Urbqf7eVrBruAd91AO1AVrupdZ9xNDDosEgfPD3S0ExKpDP141rlDPlgJLCQ8RvZaoHeSRGQXuyGhRBcrH2Q0HnRU1Rtkokexzkpuf/pNwtPvSY06X/RQYOfkhrDoaMqJjGZ4rujbja/TIA6nqgxmGQBa+epuBS1qjFt68Wk2II3hcOf0sEQe+RvEWE2OFFhp+v5/X0dhvdZx1LmjXc+Pk7iGfVgY1IuyeZn+1nzFYynsDtYvIk2WgrIzDYUp8PKnzWfGWDbXm1+zSSqeqjq/lF/6PR5MHlsSplpPdvnvc1mg35M8kaLpkrTYxHBYX6tr2dKXjvYD+63poHrQIDAQAB",
			"RSA2");


	@Test
	public void storeCreate() throws Exception {
		//3963383222 电信customerId
		QiMenApiClient client = new QiMenApiClient("http://qimen.api.taobao.com/router/qimen/service","25006245","113f2c0bd1e724664120a87269d95eb1","taobao.qimen.store.create", "809107100");
		client.setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<request>" +
			"<storeCode>mybankTest0003</storeCode>" +
			"<companyName>汇安融</companyName>" +
			"<storeName>汇安融测试门店3</storeName>" +
			"<storeType>NORMAL</storeType>" +
			"<mainCategory>70600</mainCategory>" +
			"<startTime>07:00</startTime>" +
			"<endTime>23:00</endTime>" +
			"<address>" +
			"<region>华南区</region>" +
			"<province>四川省</province>" +
			"<city>成都市</city>" +
			"<area>武侯区</area>" +
			"<town></town>" +
			"<detailAddress>拉德方式</detailAddress>" +
			"<countryCode>CN</countryCode>" +
			"</address>" +
			"<storeStatus>NORMAL</storeStatus>" +
			"<storeDescription>测试赏花</storeDescription>" +
			"<storeKeeper>" +
			"<name>张燎</name>" +
			"<tel>02883371826</tel>" +
			"<mobile>15982393775</mobile>" +
			"<fax></fax>" +
			"<zipCode></zipCode>" +
			"</storeKeeper>" +
			"<remark>测试</remark>" +
			"</request>");
		System.out.println(client.execute());
	}

	@Test
	public void storeQuery() throws Exception {
		QiMenApiClient client = new QiMenApiClient("http://qimen.api.taobao.com/router/qimen/service","25006245","113f2c0bd1e724664120a87269d95eb1","taobao.qimen.store.query", "809107100");
		client.setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<request>" +
			"<storeId>194071398</storeId>" + //汇安融测试门店3  mybankTest0003
			"</request>");
		System.out.println(client.execute());
	}

	/**
	 * 操作类型，string（50），ADD=新建，DELETE=删除，必填
	 * @throws Exception
	 */
	@Test
	public void itemStoreBanding() throws Exception {
//		575222086519
//		575108445715
//		574535292986
		QiMenApiClient client = new QiMenApiClient("http://qimen.api.taobao.com/router/qimen/service","25006245","113f2c0bd1e724664120a87269d95eb1","taobao.qimen.itemstore.banding", "809107100");
		client.setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<request>" +
			"<actionType>ADD</actionType>" +
			"<itemId>575519862798</itemId>" +
			"<storeIds>" +
			"<storeId>194071398</storeId>" + //汇安融测试门店3  mybankTest0003
			"</storeIds>" +
			"<remark>备注，string（500）</remark>" +
			"</request>");
		System.out.println(client.execute());
	}

	/**
	 * 商品关联门店查询接口
	 * @throws Exception
	 */
	@Test
	public void itemStoreQuery() throws Exception {
		QiMenApiClient client = new QiMenApiClient("http://qimen.api.taobao.com/router/qimen/service","25006245","113f2c0bd1e724664120a87269d95eb1","taobao.qimen.itemstore.query", "809107100");
		client.setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<request>" +
				"<itemId>574535292986</itemId>" +
				"<page>1</page>" +//当前页数，long，非必填，默认值为1，返回第一页数据，每页默认记录100条
				"</request>");
		System.out.println(client.execute());
	}


	/**
	 * 门店关联商品查询接口
	 * @throws Exception
	 */
	@Test
	public void storeItemQuery() throws Exception {
		QiMenApiClient client = new QiMenApiClient("http://qimen.api.taobao.com/router/qimen/service","25006245","113f2c0bd1e724664120a87269d95eb1","taobao.qimen.storeitem.query", "809107100");
		client.setBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<request>" +
				"<storeId>194071398</storeId>" + //汇安融测试门店3  mybankTest0003
				"<page>1</page>" +//当前页数，long，非必填，默认值为1，返回第一页数据，每页默认记录100条
				"</request>");
		System.out.println(client.execute());
	}

	@Test
	public void pay()throws Exception{

		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));


		AlipayTradePayRequest request = new AlipayTradePayRequest();
		//language=JSON
		String authCode = "2850742183364807486";
		request.setBizContent("{" +
			"\"out_trade_no\":\""+ System.currentTimeMillis() +"\"," +
			"\"scene\":\"bar_code\"," +
			"\"auth_code\":\""+ authCode +"\"," +
			"\"subject\":\"测试\"," +
			"\"seller_id\":\"2088002181980180\"," +
			"\"total_amount\":1," +
			"\"body\":\"测试\"," +
				"\"goods_detail\":[" +
				"{" +
				"\"goods_name\":\"HAR测试商品1vivo\"," +
				"\"quantity\":1," +
				"\"price\":1.00," +
				"\"goods_id\":\"HAR20180816_1\"" +
				"}" +
				",{" +
				"\"goods_name\":\"HAR测试商品2apple\"," +
				"\"quantity\":2," +
				"\"price\":1.00," +
				"\"goods_id\":\"HAR20180816_2\"" +
				"}" +
//				"{" +
//				"\"goods_name\":\"自由商品\"," +
//				"\"quantity\":1," +
//				"\"price\":1500.00," +
//				"\"goods_id\":\"55555\"" +
//				"}" +
				",{" +
				"\"goods_name\":\"HAR测试商品3sumsang\"," +
				"\"quantity\":3," +
				"\"price\":1500.00," +
				"\"goods_id\":\"HAR20180816_3\"" +
				"}" +
				"]," +
			"\"store_id\":\"mybankTest0003\"," +
			"\"timeout_express\":\"10m\"" +
			"}");
		long t = System.currentTimeMillis();
		AlipayTradePayResponse response = alipayClient.execute(request);
		System.out.println(System.currentTimeMillis() - t);
		System.out.println(JSON.toJSONString(response));
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}

//		2018082222001478941000327367
		System.out.println(response.getBuyerLogonId()+"\t"+ response.getBuyerUserId()+"\t"+response.getTradeNo()+"\t");

	}


	@Test
	public void close()throws Exception{

		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));


		AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
		//language=JSON
//		String authCode = "2899601722223296072";
		request.setBizContent("{" +
				"\"trade_no\":\"2018082222001478941000092241\""+
				"  }");
		long t = System.currentTimeMillis();
		AlipayTradeCloseResponse response = alipayClient.execute(request);
		System.out.println(System.currentTimeMillis() - t);
		System.out.println(JSON.toJSONString(response));
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}

//		2018082222001478941000327367
//		System.out.println(response.getBuyerLogonId()+"\t"+ response.getBuyerUserId()+"\t"+response.getTradeNo()+"\t");

	}

	@Test
	public void cancel()throws Exception{

		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));


		AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
		//language=JSON
//		String authCode = "2899601722223296072";
		request.setBizContent("{" +
				"\"trade_no\":\"2018082222001478941000329133\""+
				"  }");
		long t = System.currentTimeMillis();
		AlipayTradeCancelResponse response = alipayClient.execute(request);
		System.out.println(System.currentTimeMillis() - t);
		System.out.println(JSON.toJSONString(response));
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}

//		2018082222001478941000327367
//		System.out.println(response.getBuyerLogonId()+"\t"+ response.getBuyerUserId()+"\t"+response.getTradeNo()+"\t");

	}

	@Test
	public void precreate()throws Exception{

		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
		String authCode = "2801881534403318870";
		request.setBizContent("{" +
				"\"out_trade_no\":\""+ System.currentTimeMillis() +"\"," +
//				"\"scene\":\"bar_code\"," +
//				"\"auth_code\":\""+ authCode +"\"," +
				"\"subject\":\"测试\"," +
				"\"seller_id\":\"2088002181980180\"," +
				"\"total_amount\":0.01," +
				"\"body\":\"测试\"," +
				"\"goods_detail\":[" +
				"{" +
//			"\"goods_id\":\"574535292986\"," +
				"\"goods_name\":\"测试商品1\"," +
				"\"quantity \":1," +
				"\"price\":1.00," +
				"\"goods_id\":\"HARTEST10001\"" +
//			"\"goods_id\":\"574535292986\"" +
				"},{" +
//			"\"goods_id\":\"574535292986\"," +
				"\"goods_name\":\"测试商品2\"," +
				"\"quantity \":2," +
				"\"price\":1.00," +
				"\"goods_id\":\"HARTEST10002\"" +
//			"\"goods_id\":\"574535292986\"" +
				"},{" +
//			"\"goods_id\":\"574535292986\"," +
				"\"goods_name\":\"测试商品3\"," +
				"\"quantity \":3," +
				"\"price\":1.00," +
				"\"goods_id\":\"HARTEST10003\"" +
//			"\"goods_id\":\"574535292986\"" +
				"}" +
				"]," +
//			"\"store_id\":\"194071302\"," +
				"\"store_id\":\"mybankTest0003\"," +
				"\"timeout_express\":\"5d\"" +
				"}");
		AlipayTradePrecreateResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
//		System.out.println(response.getBuyerLogonId()+"\t"+ response.getBuyerUserId()+"\t"+response.getTradeNo()+"\t");

	}

	@Test
	public void refund()throws Exception{

		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		String outTradeNo = "2018092022001478940599279459";
		request.setBizContent("{" +
				"\"trade_no\":\""+ outTradeNo +"\"," +
//				"\"out_request_no\":\"HZ01RF001\"," +
				"\"refund_amount\":1," +
				"\"refund_reason \":\"ces\"" +
				"}");
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
		System.out.println(response.getBuyerLogonId()+"\t"+ response.getBuyerUserId()+"\t"+response.getTradeNo()+"\t");

	}

}