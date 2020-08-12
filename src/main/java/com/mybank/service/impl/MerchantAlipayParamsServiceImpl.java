package com.mybank.service.impl;

import com.mybank.base.entity.MerchantAlipayParams;
import com.mybank.base.repository.MerchantAlipayParamsRepository;
import com.mybank.service.MerchantAlipayParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/23
 */
@Service
public class MerchantAlipayParamsServiceImpl implements MerchantAlipayParamsService {

	@Autowired
	private MerchantAlipayParamsRepository repository;

	@Override
	public MerchantAlipayParams findByMerchantCode(long merchantCode){
		return repository.findById(merchantCode).orElse(null);
	}

	@Override
	public void save(MerchantAlipayParams params){
		repository.save(params);
	}

}
