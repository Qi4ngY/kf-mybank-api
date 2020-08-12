package com.mybank.api.hencm;

import com.alibaba.fastjson.JSONObject;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.service.HenCmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 河南移动
 *
 * @author huangjj
 * @create 2018-12-23 14:58
 **/
@Slf4j
@RestController
@RequestMapping(value = "/api/alipay/henCm")
public class HenCmController {

    @Autowired
    private HenCmService henCmService;

    /**
     * 检查手机号
     *
     * @param param
     * @return
     */
    @PostMapping("checkPhone")
    @KfApi(encryptable = false)
    public ResponseEntity checkPhone(@RequestBody String param) throws Exception {
        log.info("河南移动检查手机号传入参数{}", param);
        ResponseEntity responseEntity = new ResponseEntity();
        JSONObject retJson = henCmService.checkPhone(JSONObject.parseObject(param));
        log.info("河南移动检查手机号响应参数{}", retJson);
        responseEntity.setDetail(retJson.toJSONString());
        return responseEntity;
    }

    /**
     * 检验是否退订
     *
     * @param param
     * @return
     */
    @PostMapping("checkRollback")
    @KfApi(encryptable = false)
    public ResponseEntity checkRollback(@RequestBody String param) throws Exception {
        ResponseEntity responseEntity = new ResponseEntity();
        log.info("河南移动检验是否退订传入参数{}", param);
        JSONObject paramJson = JSONObject.parseObject(param);
        if (StringUtils.isBlank(paramJson.getString("phone_no"))) {
            responseEntity.setResponseCode("10001");
            responseEntity.setMsg("业务类型【phone_no】不能为空");
            return responseEntity;
        }
        if (StringUtils.isBlank(paramJson.getString("phone_no"))) {
            responseEntity.setResponseCode("10001");
            responseEntity.setMsg("业务类型【prod_prc_id】不能为空");
            return responseEntity;
        }

        JSONObject retJson = henCmService.checkRollback(JSONObject.parseObject(param));
        log.info("河南移动检验是否退订响应参数{}", retJson);
        responseEntity.setDetail(retJson.toJSONString());
        return responseEntity;
    }
}
