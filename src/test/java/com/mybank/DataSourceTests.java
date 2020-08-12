package com.mybank;

import com.mybank.base.entity.Merchant;
import com.mybank.util.MD5Util;
import com.mybank.util.SnowflakeIdWorker;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class DataSourceTests {

    static String[] code = {"2","3","4","5","6","7","8","9",
            "a","b","c","d","e","f","g","h","i","j","k","m","n","p","r","s","t","u","v","w","x","y","z"};

    static Random random = new Random();
    static String getPwd(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            sb.append(code[random.nextInt(code.length)]);
        }
        return sb.toString();
    }

    static String[] codxxxe = {"0","1","2","3","4","5","6","7","8","9",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","r","s","t","u","v","w","x","y","z",
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","R","S","T","U","V","W","X","Y","Z"};

    static String getRandomStr(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 9; i++) {
            sb.append(codxxxe[random.nextInt(codxxxe.length)]);
        }
        return sb.toString();
    }
    @Test
    public void testDataSource() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://t.kf91.cn/kf1?useUnicode=true&cmybankacterEncoding=UTF-8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=true","gavin","zl123@#%");
//        PreparedStatement merchantStmt = connection.prepareStatement("select id,third_merchant_id,service_phone from merchant where third_merchant_id not like 'CT05%' AND third_merchant_id like 'CT%' and length(service_phone) = 11");
////        PreparedStatement storeStmt = connection.prepareStatement("select id,third_store_id from store where third_store_id not like 'CT05%'");
//        PreparedStatement operStmt = connection.prepareStatement("select mo.oper_id,mo.oper_login, s.third_store_id,mo.store_id from merchant_operator mo LEFT JOIN store s on s.id = mo.store_id where mo.merchant_id = ? ");
//        ResultSet merchantRs = merchantStmt.executeQuery();
////        ResultSet shopRs = storeStmt.executeQuery();
//        ResultSet operRs;
//        String pwd;
//        String randomStr;
//        StringBuffer sb = new StringBuffer();
//        StringBuffer sms = new StringBuffer();
//        StringBuffer redisStr = new StringBuffer();
//        String adminAccount = null;
//        int j = 0;
//        while(merchantRs.next()){
//
//            operStmt.setObject(1,merchantRs.getObject("id"));
//            operRs = operStmt.executeQuery();
//            pwd = getPwd();
//
//            while (operRs.next()){
//                if(operRs.getObject("store_id") == null){
//                    adminAccount = operRs.getString("oper_login");
//                    sb.append("update merchant_operator set oper_pwd = '").append(MD5Util.encode(pwd)).append("' where oper_id = ").append(operRs.getString("oper_id")).append(";\n");
//                }else{
//                    sb.append("update merchant_operator set oper_pwd = '").append(MD5Util.encode(pwd)).append("', oper_login = '").append(operRs.getString("third_store_id")).append("' where oper_id = ").append(operRs.getString("oper_id")).append(";\n");
//                }
//            }
//            randomStr = getRandomStr();
//            sms.append(merchantRs.getString("service_phone")).append("@【汇花新零售】尊敬的商户，您的管理员账号为").append(adminAccount).append("，密码为").append(pwd).append("，为了更好的管理门店，现将您的门店账号及密码做了修改，请点击链接查看：");
//            sms.append("https://kf.huihua365.com/kf-app-api/app/shop/").append(randomStr).append("\n");
//            redisStr.append("set \"kf-app-api:randommerid").append(randomStr).append("\" \"{\\\"merId\\\":").append(merchantRs.getObject("id")).append(",\\\"pws\\\":\\\"").append(pwd).append("\\\"}\"\n");
////            if(j<5){
////                j++;
////            }else {
////                break;
////            }
//
//        }
//        System.out.println(sb);
//        System.out.println("-------------------------------------------------");
//        System.out.println(sms);
//        System.out.println("-------------------------------------------------");
//        System.out.println(redisStr);
//
//
////        PreparedStatement adminOperStmt = connection.prepareStatement("select merchant_id,id from merchant_operator where oper_login like 'a1%' and length(oper_login) = 12");
////        ResultSet adminOperResultSet = adminOperStmt.executeQuery();
////        while(adminOperResultSet.next()){
////
////        }
        StringBuffer sb = new StringBuffer();
//        PreparedStatement merchantStmt = connection.prepareStatement("select merchant_id from merchant_operator where merchant_operator.oper_login = ? limit 1");
//        for (int i = 0; i < m.length; i++) {
//            String rs = getRandomStr();
//            String pwd = getPwd();
//            sb.append("set \"kf-app-api:randommerid").append(temp[2]).append("\" \"{\\\"merId\\\":").append(resultSet.getObject("merchant_id")).append(",\\\"pws\\\":\\\"").append(temp[1]).append("\\\"}\"\n");
//
//        }
        for (String[] x:m ){
            String rs = getRandomStr();
            String pwd = getPwd();
            sb.append("setnx \"kf-app-api:randommerid").append(rs).append("\" \"{\\\"merId\\\":").append(x[1]).append(",\\\"pws\\\":\\\"").append(pwd).append("\\\"}\"   ");
            sb.append("--------------").append(x[0]).append("---------").append(MD5Util.encode(pwd)).append("\n");
        }
        System.out.println(sb);

    }

    private String[][] m ={
            {"13333403490","112142775008534528"},
            {"18925281388","112155563915386880"},
            {"18929782293","112214152776421376"},
            {"18007929050","112221009008041984"},
            {"18163815158","112229406587658240"},
            {"17763377000","112264408855650304"},
            {"18905444603","112277720871899136"},
            {"18053690888","112481337298034688"}
    };

    @Test
    public void parseId() throws Exception {
        System.out.println(SnowflakeIdWorker.parseId(109691566977400871L));

    }


}