package com.mybank.api.merchant;

import com.mybank.api.request.dto.RequestEntity;
import com.mybank.api.request.dto.merchant.MerchantCreate;
import com.mybank.api.request.dto.merchant.MerchantModify;
import com.mybank.api.request.dto.merchant.MerchantQuery;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.merchant.MerchantCreateResponse;
import com.mybank.api.response.dto.merchant.MerchantModifyResponse;
import com.mybank.api.response.dto.merchant.MerchantQueryResponse;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.base.entity.Merchant;
import com.mybank.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/17
 */
@RestController
@RequestMapping(value = "/api/merchant")
public class MerchantController {

	@Autowired
	private MerchantService merchantService;

	@PostMapping("create")
	@KfApi(method = KfApiMethodCode.KF_MERCHANT_CREATE)
	public ResponseEntity<MerchantCreateResponse> create(RequestEntity<MerchantCreate> requestEntity){
		Merchant merchant = merchantService.create(requestEntity.getDto());
		ResponseEntity<MerchantCreateResponse> entity = new ResponseEntity<>();
		entity.setBizContent(requestEntity.getBizContent());
		MerchantCreateResponse dto = new MerchantCreateResponse();
		dto.setMerchantCode(merchant.getId());
		dto.setThirdMerchantId(merchant.getThirdMerchantId());
		dto.setStatus(merchant.getStatus());
		entity.setDto(dto);
		return entity;
	}

	@PostMapping("modify")
	@KfApi(method = KfApiMethodCode.KF_MERCHANT_MODIFY)
	public ResponseEntity<MerchantModifyResponse> modify(RequestEntity<MerchantModify> requestEntity){
		ResponseEntity<MerchantModifyResponse> entity = new ResponseEntity<>();
		entity.setBizContent(requestEntity.getBizContent());
		MerchantModifyResponse dto = merchantService.modify(requestEntity.getDto());
		entity.setDto(dto);
		return entity;
	}

	@PostMapping("query")
	@KfApi(method = KfApiMethodCode.KF_MERCHANT_QUERY)
	public ResponseEntity<MerchantQueryResponse> query(RequestEntity<MerchantQuery> requestEntity){
		ResponseEntity<MerchantQueryResponse> entity = new ResponseEntity<>();
		entity.setBizContent(requestEntity.getBizContent());
		MerchantQueryResponse dto = merchantService.query(requestEntity.getDto());
		entity.setDto(dto);
		return entity;
	}

}
