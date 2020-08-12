package com.mybank.service;

import com.mybank.base.entity.App;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/20
 */
public interface AppService {

	App findByAppId(long appId);

}
