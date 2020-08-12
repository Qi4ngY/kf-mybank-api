package com.mybank.service.impl;

import com.mybank.base.entity.AppPermission;
import com.mybank.base.entity.prikey.AppPermissionKey;
import com.mybank.base.repository.AppPermissionRepository;
import com.mybank.service.AppPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/22
 */
@Service
public class AppPermissionServiceImpl implements AppPermissionService {

	@Autowired
	private AppPermissionRepository repository;

	@Override
	public AppPermission findByAppIdAndPermission(long appId, String permission) {
		if(appId < 1L || StringUtils.isEmpty(permission)){
			return null;
		}
		return repository.findById(new AppPermissionKey(appId, permission)).orElse(null);
	}
}
