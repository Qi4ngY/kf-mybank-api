package com.mybank.api.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybank.base.entity.AlipayTradeFundBill;
import com.mybank.base.repository.AlipayTradeFundBillRepository;
import com.mybank.config.SpringUtil;
import com.mybank.config.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class AlipayTrade {

    private static final Logger logger = LoggerFactory.getLogger(AlipayTrade.class);

    @Autowired
    private AlipayTradeFundBillRepository billRepository;

    @RequestMapping("/{orderNo}/{sign}")
    public String alipayCallBack(@PathVariable("orderNo") Long orderNo, @PathVariable("sign") String sign, HttpServletRequest request) {
        Enumeration<String> pns = request.getParameterNames();
        Map<String, String> treeMap = new TreeMap<>();
        while (pns.hasMoreElements()) {
            String pn = pns.nextElement();
            if (!"sign".equals(pn)) {
                treeMap.put(pn, request.getParameter(pn));
            }
        }
        logger.info("支付宝交易回调参数：{}", treeMap);
        if (!String.valueOf(orderNo).equals(SpringUtil.getBean(RedisUtils.class).get(sign))) {
            logger.warn("异常订单通知！");
        }

        String fundBillList = request.getParameter("fund_bill_list");
        if(StringUtils.isNotBlank(fundBillList)){
            JSONArray array = JSON.parseArray(fundBillList);
            array.forEach(e->{
                JSONObject jsonObject = (JSONObject) e;
                AlipayTradeFundBill bill = new AlipayTradeFundBill();
                bill.setOrderNo(orderNo);
                bill.setAmount(jsonObject.getBigDecimal("amount"));
                bill.setFundChannel(jsonObject.getString("fundChannel"));
                billRepository.save(bill);
            });
        }
        return "success";
    }

}
