package com.mybank.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/8/17
 */
public class JacksonTest {

	@Test
	public void mapSerialier()throws Exception{

		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//		mapper.set
//		mapper.
		Map<String ,Object> map = new HashMap<>();
		map.put("pageSize","10");
		map.put("createTime",new Date());
		System.out.println(mapper.writeValueAsString(map));

	}

}
