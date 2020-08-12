package com.mybank.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/19
 */
public class FileBase64ConvertUtilTest {
	@Test
	public void encodeBase64File() throws Exception {

		System.out.println(FileBase64ConvertUtil.encodeBase64File("C:\\Users\\gavin\\Desktop\\0030.png"));

	}

}