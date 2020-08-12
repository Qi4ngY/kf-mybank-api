package com.mybank.api.preauth;

import com.mybank.api.request.dto.preauth.KfAlipayPreAuthMerchantInfo;
import com.mybank.api.request.dto.preauth.KfAlipayPreAuthMerchantInfoAdd;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.perauth.KfAlipayPreAuthMerchantInfoResponse;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.base.entity.MerchantInformation;
import com.mybank.base.entity.prikey.MerchantInformationKey;
import com.mybank.base.repository.MerchantInformationRepository;
import com.mybank.util.KfStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * AlipayPreAuthMerchantController class
 *
 * @author xule
 * @date 2018/12/13
 */
@RestController
@RequestMapping(value = "/api/alipay/preauth",produces = "application/json;cmybankset=UTF-8")
@Slf4j
public class AlipayPreAuthMerchantController {

    @Autowired
    private MerchantInformationRepository merchantInformationRepository;

    @PostMapping("/getMerInfo")
    @KfApi(encryptable = false)
    public ResponseEntity<KfAlipayPreAuthMerchantInfoResponse> getMerInfo(@RequestBody @Valid KfAlipayPreAuthMerchantInfo entity) throws Exception {
        MerchantInformation information = merchantInformationRepository.findById(new MerchantInformationKey(entity.getMerCode(), entity.getBiz())).orElse(null);
        if(information == null) {
            return new ResponseEntity<>("40002", "记录不存在");
        }
        KfAlipayPreAuthMerchantInfoResponse response = new KfAlipayPreAuthMerchantInfoResponse();
        response.setBiz(information.getBiz());
        response.setLegalPhoneNo(KfStringUtils.fillStarSymbol(information.getLegalPhoneNo(),3,3));
        response.setReceiptAccountName(KfStringUtils.fillStarSymbol(information.getReceiptAccountName(),0,1));
        response.setReceiptAccountNo(KfStringUtils.fillStarSymbol(information.getReceiptAccountNo(),0,4));
        response.setIdCardNo(KfStringUtils.fillStarSymbol(information.getIdCardNo(),1,1));
        response.setMerCode(information.getMerCode());
        response.setShopCode(information.getShopCode());
        response.setOperLogin(information.getOperLogin());
        response.setCreateTime(information.getCreateTime());

        ResponseEntity<KfAlipayPreAuthMerchantInfoResponse> responseEntity = new ResponseEntity<>();
        responseEntity.setDto(response);
        return responseEntity;
    }

    @PostMapping("/addMerInfo")
    @KfApi(encryptable = false)
    public ResponseEntity<KfAlipayPreAuthMerchantInfoResponse> addMerInfo(@RequestBody @Valid KfAlipayPreAuthMerchantInfoAdd entity) throws Exception {
        MerchantInformation information = merchantInformationRepository.findById(new MerchantInformationKey(entity.getMerCode(), entity.getBiz())).orElse(null);
        KfAlipayPreAuthMerchantInfoResponse response = new KfAlipayPreAuthMerchantInfoResponse();
        if(information == null) {
            information = new MerchantInformation();
            information.setMerCode(entity.getMerCode());
            information.setShopCode(entity.getShopCode());
            information.setReceiptAccountNo(entity.getReceiptAccountNo());
            information.setReceiptAccountName(entity.getReceiptAccountName());
            information.setLegalPhoneNo(entity.getLegalPhoneNo());
            information.setIdCardNo(entity.getIdCardNo());
            information.setOperLogin(entity.getOperLogin());
            information.setBiz(entity.getBiz());
            information.setCreateTime(new Date());
            merchantInformationRepository.saveAndFlush(information);
        }
        response.setBiz(information.getBiz());
        response.setLegalPhoneNo(KfStringUtils.fillStarSymbol(information.getLegalPhoneNo(),3,3));
        response.setReceiptAccountName(KfStringUtils.fillStarSymbol(information.getReceiptAccountName(),0,1));
        response.setReceiptAccountNo(KfStringUtils.fillStarSymbol(information.getReceiptAccountNo(),0,4));
        response.setIdCardNo(KfStringUtils.fillStarSymbol(information.getIdCardNo(),1,1));
        response.setMerCode(information.getMerCode());
        response.setShopCode(information.getShopCode());
        response.setOperLogin(information.getOperLogin());
        response.setCreateTime(information.getCreateTime());

        ResponseEntity<KfAlipayPreAuthMerchantInfoResponse> responseEntity = new ResponseEntity<>();
        responseEntity.setDto(response);
        return responseEntity;
    }
}
