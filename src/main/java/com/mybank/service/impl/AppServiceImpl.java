package com.mybank.service.impl;

import com.mybank.base.entity.App;
import com.mybank.base.repository.AppRepository;
import com.mybank.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/20
 */
@Service
public class AppServiceImpl implements AppService {


	@Autowired
	private AppRepository repository;

	@Override
//	@Cacheable(value="apps",key="#appId+'findByAppId'")
	public App findByAppId(long appId) {
		return repository.findById(appId).orElse(null);
	}
}
