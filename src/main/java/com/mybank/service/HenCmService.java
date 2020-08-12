package com.mybank.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author huangjj
 * @create 2018-12-23 15:09
 **/

public interface HenCmService {
    /**
     * 检查手机号
     *
     * @param param
     * @return
     * @throws Exception
     */
    JSONObject checkPhone(JSONObject param) throws Exception;

    /**
     * 检验是否退订
     *
     * @param param
     * @return
     * @throws Exception
     */
    JSONObject checkRollback(JSONObject param) throws Exception;
}
