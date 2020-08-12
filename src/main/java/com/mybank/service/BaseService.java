package com.mybank.service;

import com.mybank.api.response.dto.ResponseEntity;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/23
 */
public interface BaseService {

	ResponseEntity validateMerchant(Long merchantCode, String thirdMerchantId);

	ResponseEntity validateMerchantAlipayParams(long merchantCode);

}
