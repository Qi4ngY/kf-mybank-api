package com.mybank.service.impl;

import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.base.entity.Merchant;
import com.mybank.base.entity.MerchantAlipayParams;
import com.mybank.base.repository.MerchantAlipayParamsRepository;
import com.mybank.base.repository.MerchantRepository;
import com.mybank.config.RedisUtils;
import com.mybank.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/23
 */
public class BaseServiceImpl implements BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private MerchantAlipayParamsRepository alipayParamsRepository;

	@Autowired
	private RedisUtils redisUtils;

	@Override
	public ResponseEntity validateMerchant(Long merchantCode, String thirdMerchantId) {
		Merchant merchant;
		if(merchantCode != null && merchantCode != 0){
			merchant = merchantRepository.findById(merchantCode).orElse(null);
		}else{
			merchant = merchantRepository.findTopByThirdMerchantIdAndAppId(thirdMerchantId,
					ThreadLocalParams.getInstance().getApp().getAppId());
		}

		if(merchant == null || merchant.getStatus() != 1){
			ResponseEntity responseEntity = ThreadLocalParams.getInstance().getResponse();
			responseEntity.setResponseCode("40001");
			responseEntity.setMsg("商户不存在或者商户状态异常！");
			return responseEntity;
		}

        if(!ThreadLocalParams.getInstance().getApp().getAppId().equals(merchant.getAppId())){
            ResponseEntity responseEntity = ThreadLocalParams.getInstance().getResponse();
            responseEntity.setResponseCode("40001");
            responseEntity.setMsg("商户不存在！");
            return responseEntity;
        }

        String isUpgrade = redisUtils.get("kf:upgrade");
        if("true".equalsIgnoreCase(isUpgrade) && !"377117".equals(merchant.getThirdMerchantId())){
            ResponseEntity responseEntity = ThreadLocalParams.getInstance().getResponse();
            responseEntity.setResponseCode("40001");
            responseEntity.setMsg("系统升级中....！");
            return responseEntity;
        }

		ThreadLocalParams.getInstance().putThreadVar("Merchant",merchant);
		return null;
	}

	@Override
	public ResponseEntity validateMerchantAlipayParams(long merchantCode){
		MerchantAlipayParams params = alipayParamsRepository.findById(merchantCode).orElse(null);
		//参数,pid,账号，应用为空
		if(params == null ||
			StringUtils.isBlank(params.getAlipayUserId()) ||
			StringUtils.isBlank(params.getAlipayLogonId()) ||
			StringUtils.isBlank(params.getAlipayAppId())){
			ResponseEntity responseEntity = ThreadLocalParams.getInstance().getResponse();
			responseEntity.setResponseCode("40001");
			responseEntity.setMsg("商户支付宝参数配置异常！");
			return responseEntity;
		}
        logger.info("请用支付宝应用id：【{}】，收款账号：【{}】，收款PID：【{}】",params.getAlipayAppId(), params.getAlipayLogonId(), params.getAlipayUserId());
		ThreadLocalParams.getInstance().putThreadVar("MerchantAlipayParams",params);
		return null;
	}


}
