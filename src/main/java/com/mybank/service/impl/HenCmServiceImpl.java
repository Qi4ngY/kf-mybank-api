package com.mybank.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybank.service.HenCmService;
import com.mybank.util.hencm.HnCmccUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author huangjj
 * @create 2018-12-23 15:14
 **/
@Slf4j
@Service
public class HenCmServiceImpl implements HenCmService {
    @Override
    public JSONObject checkPhone(JSONObject param) throws Exception {
        //返回参数
        JSONObject reqJson = new JSONObject();
        reqJson.put("SVC_NUM", param.getString("phone_no"));
        reqJson.put("OFFER_ID", param.getString("prod_prc_id"));
        log.info("调用河南移动营销活动预校验接口传入参数{}", reqJson);
        JSONObject respJson = HnCmccUtils.checkCanOrder(reqJson);
        log.info("调用河南移动营销活动预校验接口响应参数{}", respJson);
        return reqJson;
    }

    @Override
    public JSONObject checkRollback(JSONObject param) throws Exception {
        //返回参数
        JSONObject retJson = new JSONObject();
        JSONObject reqJson = new JSONObject();
        reqJson.put("SvcNum", param.getString("phone_no"));
        reqJson.put("OfferId", param.getString("prod_prc_id"));
        reqJson.put("OprType", "ROLLBACK");//操作类型(ORDER：订购,ROLLBACK：取消订购)
        log.info("调用河南移动营销活动预校验接口传入参数{}", reqJson);
        JSONObject respJson = HnCmccUtils.orderOrCancel(reqJson);
        log.info("调用河南移动营销活动预校验接口响应参数{}", respJson);
        if (!Objects.isNull(respJson) && !Objects.isNull(respJson.getString("SO_MEMBER_DEAL"))) {
            retJson.put("flag", respJson.getJSONObject("SO_MEMBER_DEAL").getString("FLAG") == "Y" ? true : false);
            retJson.put("msg", respJson.getJSONObject("SO_MEMBER_DEAL").getString("MESSAGE"));
        }
        return retJson;
    }
}
