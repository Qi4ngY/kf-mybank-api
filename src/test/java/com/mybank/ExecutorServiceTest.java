package com.mybank;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayUserInfoSmybankeRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayUserInfoSmybankeResponse;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/23
 */
public class ExecutorServiceTest {

	ExecutorService executorService = Executors.newCachedThreadPool();

	@Test
	public void test() throws Exception {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
				"2018032302433722",
				"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCppZy01HnVP03mMa54LBOoWaW6Tk8Qug0UDhEUrRBNFWaT+MstxpUpanlMp2ZIC9gQVdsduexUzQUa1QN5CebZkxVudMA3DQS2eXRYDlZEE8WIJoGUQ/pSYGHDDi1pWITN2UE6bLlnmKhoYpchKX7iWmx5k3JXynGlHrHRG+s9qHROfR131k03fmUWv50WGYsj2+yxDcoA+gjvXIIIqS6ITpZjw/YzrC9PzlH+L+PSklIe1/rgeEKm83W2iln7VlwdufOENEzA8XGZJq1XM+VsCgsZyh7OBagSdIqX22WukVptHXrOG4HL0nifCSpNVUUnKPJzCUtBDIkrKViCdKg5AgMBAAECggEAOi7nsQlSg7XDdNjDOsn7L/kXIu+qpFT7GU69wvacKaPWW5evr5J1JBA1a/nZ1YniX6/ih651hJwQTSSTnVatCyAr8IcwSXE4lZDQO0cnhQ+25rw2IvCxkMEnpmtdXc0TNzp06aHScNpln+94X9JIhz92BrhwNfXjxjrhx5MouitVb54WkcDPWEHASCs7V6c9m5Jm06dkCahQKpcgkZfHqAXjnYJbrmpkz0VO5FPE1sCo4Ns5GOFNESuSkzWcQpymACaX/uvO2WG+R5o/zgLMannTbTTH3BpL6EGos+DwGTDIAlGmMkkDF7epG9As1SMSFYsoS6KceAzQi/7hYadsEQKBgQDopTYecevWcA5nHdNRuxXtc8JP/1OpBvsHY5ODQxQpjr0b5bzIUWVUbnrG7Q5sGqDyIyYw7Ll4blMbHvLdWy65/5bD8UWKH2BRg13CmtXkaA2iDVUNLqYue5hcVpbP5N4kYY9Ac+nU0W1n+1edsvliluea3uNK/0M5l9VEfe6irQKBgQC6rWTyv0NSuCnFKa/JtggfinJO8CN8BeThdpA0q3NoCtQTmII9YEbuoh0cgKjoo4ErqmseOKWQHk89x4cXk32mjO1NAn6IE3Fl9Iti+3sCCZkkKkLUUIuEiiSeLDOajwAWYaQFe76x+EGibhSPvi4EbbstM3b3mtkxOZBEWSkZPQKBgEKUpbxo1hn0z+pq+sN1S309l+rVI9hICye+M8VpOSf7lduaEEE0Kk/Tnoe36Fy/8jfaH1/5H8t7p4UnglIfemv43ekN79zrxeEOO5MqRPStYtlx/1ladGzxKlWi4l51Ha5DzX+grNcfaLRNlnGnev/gOEB2cY5RJ6gf8AU+FUGxAoGAGjPieoxMN90vx8P9L7QEWeEGAHyGjo8JPHh2712tekoi0IlUpqbP7gkFSw3oJ5djtyxZksliu+L1Pne2R6HlPi6D1I5oElOgVC5KLl+cOYH9Y+0mqkSQAM+fKjCMIcK2wW19J2PiNXriQ5lt+2s3bFm2rzaNGV475EPvMxjMwwUCgYEArvqOx0esekKKhkTVQcGTZ9Cpr0VI13xASZHZnPbRgy8Zo2fVjDb2mRM1enX34U5UTbY9jNvjQc3Ka8PzYnlvzfAWoW7s8ApsP88x2E9vyT39FqeGSv4l3dXF7dJ68FugyIypzXGW1rENJ+KrJaph6DVnvQ6PfLhS7Imi0SKc03E=",
				"json", "utf-8",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjHMY+Urbqf7eVrBruAd91AO1AVrupdZ9xNDDosEgfPD3S0ExKpDP141rlDPlgJLCQ8RvZaoHeSRGQXuyGhRBcrH2Q0HnRU1Rtkokexzkpuf/pNwtPvSY06X/RQYOfkhrDoaMqJjGZ4rujbja/TIA6nqgxmGQBa+epuBS1qjFt68Wk2II3hcOf0sEQe+RvEWE2OFFhp+v5/X0dhvdZx1LmjXc+Pk7iGfVgY1IuyeZn+1nzFYynsDtYvIk2WgrIzDYUp8PKnzWfGWDbXm1+zSSqeqjq/lF/6PR5MHlsSplpPdvnvc1mg35M8kaLpkrTYxHBYX6tr2dKXjvYD+63poHrQIDAQAB",
				"RSA2");
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

		AlipayTradeQueryModel mo = new AlipayTradeQueryModel();
		mo.setOutTradeNo("");

		request.setBizContent("{\"out_trade_no\":\"111776855350317056\"}");

		AlipayTradeQueryResponse response = alipayClient.execute(request);
		System.out.println(JSON.toJSONString(response));
	}


}
