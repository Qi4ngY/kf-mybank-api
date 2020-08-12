package com.mybank.service;

import com.mybank.api.request.dto.merchant.MerchantCreate;
import com.mybank.api.request.dto.merchant.MerchantModify;
import com.mybank.api.request.dto.merchant.MerchantQuery;
import com.mybank.api.response.dto.merchant.MerchantModifyResponse;
import com.mybank.api.response.dto.merchant.MerchantQueryResponse;
import com.mybank.base.entity.Merchant;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/19
 */
public interface MerchantService {

	Merchant create(MerchantCreate dto);

	MerchantQueryResponse query(MerchantQuery dto);

	MerchantModifyResponse modify(MerchantModify dto);
}
