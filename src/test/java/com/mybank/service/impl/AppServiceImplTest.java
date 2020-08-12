package com.mybank.service.impl;

import com.alibaba.fastjson.JSON;
import com.mybank.api.response.dto.merchant.MerchantCreateResponse;
import com.mybank.base.entity.App;
import com.mybank.secret.XRsa;
import com.mybank.service.AppService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppServiceImplTest {

    @Autowired
    private AppService service;

    @Test
    @Transactional//解决 no session
    public void findByAppId(){

//        App app = service.findByAppId("10006");
//
//        XRsa rsa = new XRsa(app.getThirdPubKey(), app.getPriKey());
//        System.out.println(rsa.sign("{\"percent\":100,\"stageNum\":3,\"price\":\"60.00\",\"authCode\":\"123465798123465132\",\"merchantCode\":\"123465\",\"thirdOrderNo\":\"123456798\",\"thirdNotifyUrl\":\"http://baidu.com\",\"subject\":\"ceshi\"}"));
//        MerchantCreateResponse response = new MerchantCreateResponse();
//        response.setMerchantCode("hello");
//        response.setStatus(2);
//        System.out.println(response.sign(rsa));
    }

}