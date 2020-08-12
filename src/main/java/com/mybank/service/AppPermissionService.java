package com.mybank.service;

import com.mybank.base.entity.AppPermission;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/5/22
 */
public interface AppPermissionService {

	AppPermission findByAppIdAndPermission(long appId, String permission);

}
