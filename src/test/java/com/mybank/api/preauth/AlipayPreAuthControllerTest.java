package com.mybank.api.preauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mybank.HarAlipayApiApplication;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.base.entity.constant.OrderTradeType;
import com.mybank.base.repository.AlipayPreAuthOrderDetailRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/15
 */
//@SpringBootTest(classes = HarAlipayApiApplication.class)
//@RunWith(value = SpringRunner.class)
public class AlipayPreAuthControllerTest {

//	@Autowired
//	private AlipayPreAuthOrderDetailRepository alipayPreAuthOrderDetailRepository;

	//	@Autowired
	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void findPage() throws Exception {
	}

	class User {
		String contactPhoneNo;

		public String getContactPhoneNo() {
			return contactPhoneNo;
		}

		public void setContactPhoneNo(String contactPhoneNo) {
			this.contactPhoneNo = contactPhoneNo;
		}
	}

	@Test
	public void freezeTest() throws Exception {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306404",
				"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
				"json", "GBK",
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
				"RSA");

		AlipayFundAuthOrderVoucherCreateRequest request = new AlipayFundAuthOrderVoucherCreateRequest();
		request.setBizContent("{" +
				"\"out_order_no\":\"" + System.currentTimeMillis() + "\"," +
				"\"out_request_no\":\"" + System.currentTimeMillis() + "\"," +
				"\"payee_logon_id\":\"zliao520@gmail.com\"," +
				"\"order_title\":\"预授权发码\"," +
				"\"amount\":10," +
				"\"pay_timeout\":\"2m\"," +
//				"\"extra_param\":\"{\\\"category\\\":\\\"HOTEL\\\"}\"," +
				"\"product_code\":\"PRE_AUTH\"" +
				",\"enable_pay_channels\":\"[{\\\"payChannelType\\\":\\\"PCREDIT_PAY\\\"},{\\\"payChannelType\\\":\\\"MONEY_FUND\\\"}]\"" +
				"}");
		System.out.println(request.getBizContent());
		AlipayFundAuthOrderVoucherCreateResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
		if (response.isSuccess()) {
			System.out.println(response.getCodeValue());
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}

	@Test
	public void freezePayTest() throws Exception {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2018032302433722",
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
				"json", "GBK",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjHMY+Urbqf7eVrBruAd91AO1AVrupdZ9xNDDosEgfPD3S0ExKpDP141rlDPlgJLCQ8RvZaoHeSRGQXuyGhRBcrH2Q0HnRU1Rtkokexzkpuf/pNwtPvSY06X/RQYOfkhrDoaMqJjGZ4rujbja/TIA6nqgxmGQBa+epuBS1qjFt68Wk2II3hcOf0sEQe+RvEWE2OFFhp+v5/X0dhvdZx1LmjXc+Pk7iGfVgY1IuyeZn+1nzFYynsDtYvIk2WgrIzDYUp8PKnzWfGWDbXm1+zSSqeqjq/lF/6PR5MHlsSplpPdvnvc1mg35M8kaLpkrTYxHBYX6tr2dKXjvYD+63poHrQIDAQAB",
				"RSA2");


		AlipayTradePayRequest request = new AlipayTradePayRequest();
		request.setBizContent("{\"auth_code\":\"2823528694762535253\",\"body\":\"商品\",\"disable_pay_channels\":\"pcredit,pcreditpayInstallment\"," +
				"\"extend_params\":{\"sys_service_provider_id\":\"2088821153023555\"}," +
				"\"goods_detail\":[{\"goods_id\":\"93046700041736192181022113018172\",\"goods_name\":\"测试商品10\",\"price\":\"0.10\",\"quantity\":1}]," +
				"\"out_trade_no\":\""+System.currentTimeMillis()+"\",\"scene\":\"bar_code\",\"seller_id\":\"2088512812178942\"," +
				"\"store_id\":\"11183653\",\"subject\":\"商品\",\"timeout_express\":\"5m\",\"total_amount\":\"1.10\"}");
		AlipayTradePayResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
		if (response.isSuccess()) {

			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}

	@Test
	public void freezeRefundTest() throws Exception {
//		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017050507129426",
//                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
//                "json", "GBK",
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
//                "RSA");
        //jrkj10@huianrong.com
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2018032302433851",
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
//                "json", "GBK",
//                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo4AcSB5HSr9dHMhJknnyI6aFJNa6LSjW5cXMrr0cR4a0P72XV1rLDoKpAi3Uia6sLZqBjkbzG6jskZUr4C9sq15tZa3x5dwccWX+HW4YnCYLyAB0P+Jah5xEtmNMqSTmbs6RQp2kDMX1xoUinaYiQudxAapw10vr6bli6vPaOxxs5qVJ4jj1jpZKH/FSSyim+8BQG1521YMsriZPjkc1OhQdxnC5M4i3IlV+/jxsg5ZwvUa9dHQmbZTZdPCtpAmt8hA/ceDDSCSGHu/ZSHHCWidMuYdLaGIkaoCipIVOygFZY6t+V/69r5Lbk2NYWn01IcsNWzTFvj0/JWATm1434wIDAQAB",
//                "RSA2");

        //jrkj2@huianrong.com
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2018032302432256",
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
//                "json", "GBK",
//                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlh41oYSHRehEwQdMQ9fuo78TIWYqycx7Z4QZhuOCcKcG8AY0ol+qI/jyfYF+XssRU+CM1uTz+i9JcNJgEGPT2uavclzr5mamBkXS39EH1ASFXRz7b+EizE+DaH9oWa/fKAwq9Fd1DqSH6jfXhWvPoj6QeqK+5+a5Sk4KJmqmLze1pRLk7UpwgXccx8nnlPMYF9Z2Ww6bKwM4gobV5JAHAkFcy2sPBqrW9oy0OQm0+MeyFu3918YsGZsL/sBOtrep1+SR+WXgn+FbQuUbz3qwUI1s+MXJGvT2eexLXHWQU+IvTBuxvFFwykwO/b1i455ldMUPuna+XtrWgYztuhUj6wIDAQAB",
//                "RSA2");

        //scmybank6@huianrong.com
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306436",
        "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
        "json", "GBK",
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
        "RSA");

        //scmybank1@huianrong.com
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306200",
//                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
//                "json", "GBK",
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
//                "RSA");
//        133290965116141568	2018032302432256	853.33
////        133870511490088960	2018032302432256	853.33
////        134281662728257536	2018032302432256	1493.33
////        134627684222521344	2018032302432256	853.33
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		request.setBizContent("{" +
				"    \"refund_reason\":\"正常退款\"," +
				"    \"trade_no\":\"2019010322001494210558889685\"," +
				"    \"refund_amount\":\"1\"" +
				"}");
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
		if (response.isSuccess()) {

			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}

	@Test
	public void freezeQuery() throws Exception {

		String[] appIds = {
				"2017082108306282",
				 "2017082108306343",
				 "2017082108306404",
				 "2017082108306436"//,
//				"2018031602385788"

		};
//		订单号2018082110002001210267967091商户订单号84366509115445248金额1.9
//		订单号2018082110002001210266731915商户订单号84365793470713856金额1.9
//		订单号2018082110002001210266433092商户订单号84356739851620352金额1.9
//		授权号2018082110002001210266206730订单号84278401770459136金额2.9
		String[][] orderNOs = {
//				{"2018080610002001940225984768", "20180806673830079402"},
//				{"84366509115445248", "20180807726598379402"},
//				{"84365793470713856", "20180807750265309402"},
//				{"84356739851620352", "20180808795361449402"},
				{"2018082110002001210267214203", "20180821947515482102"}
		};
		a:for (String appId : appIds) {
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId,
					"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
					"json", "GBK",
					"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
					"RSA");
//
//			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId,
//					"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
//					"json", "GBK",
//					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtsH67nemLbxPsA+mWitndJM6+/lKUoJHOPG2QQhTVqml+GKF9+vB1Tf1xyXQk92B4Hgm2rQV8BjMAs9No/sRgD4ke/QhffudBK3xDtHbQ0zG0xyuz1/dc1goEK2FaATNgG8gjz5vGlZ1gMKLVb17mStiKOaXTcmvAyGIDrxqXwH7TwLtwoVZWSGpRrmxXmgG/49CF1rHTIzzAiZyb5hoU0PEchlL82W94q1tWGlevrWFn0BdSYHr33R3dmlvQPGFrGo7f640UDrQ9HLZOK9kVD9MXo4Ii1c16zOhzpvkaouoSAJIhZu1q2Z8zKbjSoYfD3b0HQq97TV0s8Nm5okYywIDAQAB",
//					"RSA2");
			AlipayFundAuthOperationDetailQueryRequest request = new AlipayFundAuthOperationDetailQueryRequest();
			for (String[] orderNo : orderNOs) {
				request.setBizContent("{" +
						"\"auth_no\":\"" + orderNo[0] + "\"," +
						"\"operation_id\":\"" + orderNo[1] + "\"" +
//						"\"out_order_no\":\"" + orderNo[0] + "\"," +
//						"\"out_request_no\":\"" + orderNo[0] + "\"" +
						"}");
				AlipayFundAuthOperationDetailQueryResponse response = alipayClient.execute(request);
				if (response.isSuccess()) {
					System.out.println("---------------------------查询调用成功------------------------------\n"+JSON.toJSONString(response));
//					System.out.println(JSON.toJSONString(response));
					AlipayFundAuthOrderUnfreezeRequest request1 = new AlipayFundAuthOrderUnfreezeRequest();
					request1.setBizContent("{" +
							"\"auth_no\":\""+ response.getAuthNo() +"\"," +
							"\"out_request_no\":\""+ response.getOperationId() +"\"," +
							"\"amount\":" + response.getRestAmount()+ "," +
							"\"remark\":\"授权撤销\"" +
							"  }");
					AlipayFundAuthOrderUnfreezeResponse response1 = alipayClient.execute(request1);
					if (response1.isSuccess()) {
						System.out.println("---------------------------解冻调用成功------------------------------"+response.getRestAmount());
						continue a;
					}
//
				}

			}
		}
	}
//	auth_no:2018082110002001210266444849,amount:1.90,out_request_no:84292629143228416,operatorId:20180821940264912102
//	auth_no:2018082110002001210266043165,amount:1.90,out_request_no:84280242587570176,operatorId:20180821941402012102
//	auth_no:2018082110002001210267238997,amount:1.90,out_request_no:84279973560717312,operatorId:20180821948133362102
//	auth_no:2018082010002001210265394605,amount:1.00,out_request_no:83945370463506432,operatorId:20180820928913812102
	@Test
	public void freezeCancelTest() throws Exception {
//		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306282",
//				"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
//				"json", "GBK",
//				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
//				"RSA");

        //scmybank6
//		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306436",
//				"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
//				"json", "GBK",
//				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
//				"RSA");


//        //scmybank1@huianrong.com
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306200",
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
                "json", "GBK",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
                "RSA");


        AlipayFundAuthOrderUnfreezeRequest request = new AlipayFundAuthOrderUnfreezeRequest();
        request.setBizContent("{" +
                "\"auth_no\":\"2019010710002001940247525328\"," +
                "\"out_request_no\":\""+ System.currentTimeMillis() +"\"," +
                "\"amount\":480," +
                "\"remark\":\"测试授权撤销\"" +
                "  }");
        System.out.println(request.getBizContent());
        AlipayFundAuthOrderUnfreezeResponse response = alipayClient.execute(request);
        System.out.println(JSON.toJSONString(response));
        if (response.isSuccess()) {
            System.out.println("调用成功");
        }

	}

    @Test
    public void alipayTradeQuery() throws Exception {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2018032302432256",
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
				"json", "GBK",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlh41oYSHRehEwQdMQ9fuo78TIWYqycx7Z4QZhuOCcKcG8AY0ol+qI/jyfYF+XssRU+CM1uTz+i9JcNJgEGPT2uavclzr5mamBkXS39EH1ASFXRz7b+EizE+DaH9oWa/fKAwq9Fd1DqSH6jfXhWvPoj6QeqK+5+a5Sk4KJmqmLze1pRLk7UpwgXccx8nnlPMYF9Z2Ww6bKwM4gobV5JAHAkFcy2sPBqrW9oy0OQm0+MeyFu3918YsGZsL/sBOtrep1+SR+WXgn+FbQuUbz3qwUI1s+MXJGvT2eexLXHWQU+IvTBuxvFFwykwO/b1i455ldMUPuna+XtrWgYztuhUj6wIDAQAB",
				"RSA2");

//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017082108306436",
//                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtyKGAX+U7kfPcw2kS7Cku3d3kNOxjG37T1Ckt43tH/F41QQ/x0HuJs5QoeRkW1Y3UZ0AR2H/Oq4GkkI+tZnxFONx3Pkl/qhuNu/0fv3QEbAcFF7meKY6/9gbRs+ZwmMfazR6U1f4S6ML3+Ee/1NVYuiqKQF2uViXJ0keMS5pPZfQTM2SL+/TgH4YD5ha1iRFgOsLasjonV6PmSfrkpltcHlWdkN+CpSWOCGmDGvzcw6Z91PHVvmGGErFUV6ybJywXtGrf4VG5ijVQOA9FDFf4q0IyG5mlYrurMx09KysfqSkshAequLmU390md9tKWWTjZMgMVXwQz9GG8MAJg8FnAgMBAAECggEAcxNIxl45yDGEnuoS+QHC35LxLP1SjuoVGKYDwqxA5VEa/P6hVUntlCiC8i4GTBWLflwElAydzTfKeDN1wKf9oETf0GJBfzUgW8FhAxXe+FKkb13ffvkb2sdKirAq5uJ0bNGo73uoa9MPz+3rbP69a6yTIMswfZH0IemJkUA65VmRUw2rFuxmbrWuGgL7ZoUm7cIxXiX+TZGhf9+kp71icDoEtJiC8ZFdjapBZmcqsoXm0YGRmY8+tSt2rY3aDDIXEZTWwk4RA3NQ/pHrJ+h3yr+sWXdrIFLRLQm3Oy53zNrkt+eisHl62ACwG2qg2GeUZuHEYBaNpVz8Zp4flDyqAQKBgQDYDPLN3rGtrPGNJVFyYpwSGhhqLwy6PqaPU4pPup3XYY9UtkZIf4WiX+u/8KV2vLoArXhdp9tDztqatzidUHhDQbffYIANecbAsoOP/dnjJJmLOGQrGX7pV9YqExk91AGM+u3BJMTwMA1dgn/urf81LC5EtWbxfDmqPih3oE30ZwKBgQDN6uwSiQakyY0MP+P1L09iZVPWpGT6tie4ncLncCwlBL5X9qy8FMnyQ7Wcp77NEBDEDMb0kAq+gMOhc/8AOa/1PlQ53DC6QAhsn59tb0DyNf2Cj9GPni7iPHUy/IsKOPHgH0Y754Po6NPXyf0+Xvz1lwDWF4qSdkYLzBRroEOrAQKBgENt1yHVjRKtVT4+FWtjx14G8vPA93HxN76nzhUQI08jTb6cnNbnpeeFlGh1TM8AG/U2LsjcKJzDJIsBABRpFv+2tRJsZcrO5O8jR9ha2/P+akV/NhQVgvyEw3yiNVCMqGc9sZKETUrBVIqIvjDZ8TrBLTeYfiaEJOMzPyIG0A2lAoGAC4pkCrHlTm0BW7Nk+kPZhiE5rqjuA+MbnMwytTfjMXvvkwx/J9debhZ/YjVAi0ugNOymZeSAxaZ/0Feo7gFNrEf+/nMpw/Z8wmG71K1MjYabG6slyo/J/uH2i5H91OfWHoKCNC9IhFAwN0LZz5oRQU5iPJ17JbQ9PsIETvffcwECgYAEZ7H5gw+zzgYQUSRexgPCc5lscJGL/kQ3KGx4nwAl8WsvF77nfccd+BpQRqrLaRzAgyATEPuEoclKtdFIWVRzZtIxi3wh21M/te6NMfd59u1duRAWSdF4D0fAt4Q+u/Es9SK0h9Bi5fj1+hwnbMO0+Bv7Fx7gvS2eylYgjZLFsw==",
//                "json", "GBK",
//                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB",
//                "RSA");



            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"181230174409450026\"" +
                "  }");
        System.out.println(request.getBizContent());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        System.out.println(JSON.toJSONString(response));
        if (response.isSuccess()) {
            System.out.println("调用成功");
        }
    }



















}