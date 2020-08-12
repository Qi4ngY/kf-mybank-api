package com.mybank.alipay.cashlessvoucher;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/19
 */
public class CreateCashlessVoucherTest {

	CreateCashlessVoucher voucher;

	@Before
	public void before(){
		voucher = new CreateCashlessVoucher();
	}

	@Test
	public void create() throws Exception {
		System.out.println(JSON.toJSONString(voucher.create("zhifu-yd_1@huianrong.com")));
	}

	@Test
	public void query() throws Exception {
	}

	@Test
	public void send() throws Exception {
//		2018041900073002779501EM2DFP
		System.out.println(JSON.toJSONString(voucher.send("zhifu-yd_1@huianrong.com")));
	}

	@Test
	public void pay() throws Exception {
		System.out.println(JSON.toJSONString(voucher.pay("zhifu-yd_1@huianrong.com")));
	}

	@Test
	public void refund() throws Exception {
		System.out.println(JSON.toJSONString(voucher.refund("zhifu-yd_1@huianrong.com")));
	}

}