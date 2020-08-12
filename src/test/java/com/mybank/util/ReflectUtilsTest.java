package com.mybank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtilsTest {
    @Test
    public void parseObjectWithoutParentProperty() throws Exception {
//
//        MerchantCreateResponse response = new MerchantCreateResponse();
//        response.setMerchantCode("hello");
//        response.setStatus(2);
//        System.out.println(ReflectUtils.parseObjectWithoutParentPropertyToJsonStr(response));



    }

    @Test
    public void testGetEnumConstListByPackage()throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(ReflectUtils.getEnumConstMapByPackage("com.mybank.base.entity.constant")));
        System.out.println(mapper.writeValueAsString(ReflectUtils.getEnumConstListByPackage("com.mybank.base.entity.constant")));
    }

    @Test
    public void getMethodGenericClass()throws Exception{
        Method[] methods = null;// AlipayStageTradeController.class.getMethods();
        for(Method method : methods){
            if("stageTrade".equals(method.getName())){

                Type[] types = method.getGenericParameterTypes();// 获取参数，可能是多个，所以是数组
                for (Type type2 : types) {
                    if (type2 instanceof ParameterizedType){// 判断获取的类型是否是参数类型
                        Type[] typetwos = ((ParameterizedType) type2).getActualTypeArguments();// 强制转型为带参数的泛型类型，
                        for (Type type3 : typetwos) {
                            System.out.println("参数类型" + Class.forName("com.mybank.api.request.dto.trade.stage.AlipayStageTrade"));
                            System.out.println("参数类型" + type3);
                        }
                    }
                }
                break;
            }
        }
    }

}