package com.mybank.alipay.retail;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.FileItem;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.OmniitemItemImageUploadRequest;
import com.taobao.api.request.OmniitemItemPublishRequest;
import com.taobao.api.response.OmniitemItemImageUploadResponse;
import com.taobao.api.response.OmniitemItemPublishResponse;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.taobao.api.request.OmniitemItemPublishRequest.*;

/**
 * MainClass class
 *
 * @author xule
 * @date 2018/07/20
 */
public class ItemTest {

    private static String url= "http://gw.api.taobao.com/router/rest";
    private static String appkey= "25006245";
    private static String secret= "113f2c0bd1e724664120a87269d95eb1";
    private static String sessionKey = "6100902b28146176ddf8648c8ba432ee7a21cf16bc76b59809107100";

    @Test
    public void createItem() throws ApiException {

        int no = 4;
        String brand = "VIVO X20";

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        OmniitemItemPublishRequest req = new OmniitemItemPublishRequest();
        ItemLightPublishDto obj1 = new ItemLightPublishDto();

        List<ItemLightPublishSalePropDto> saleProps = new ArrayList<>();
        ItemLightPublishSalePropDto obj11 = new ItemLightPublishSalePropDto();
        obj11.setPid(10016L);
        obj11.setValue(brand);
        saleProps.add(obj11);

        ItemLightPublishSkuDto obj7 = new ItemLightPublishSkuDto();
        obj7.setSaleProps(saleProps);
        obj7.setSkuOuterId("HAR"+new SimpleDateFormat("yyyyMMdd").format(new Date())+"_"+no);
        obj7.setPrice("100.00");
        List<ItemLightPublishSkuDto> sku = new ArrayList<>();
        sku.add(obj7);

        List<ItemLightPublishImageDto> list3 = new ArrayList<>();
        ItemLightPublishImageDto obj4 = new ItemLightPublishImageDto();
        list3.add(obj4);
        obj4.setUrl("i3/4036944301/TB2._pfFH5YBuNjSspoXXbeNFXa_!!4036944301.png");

//        obj1.setImages(list3);
        obj1.setCatId(126526001L);
        obj1.setPrice("100.00");
        obj1.setSkus(sku);
        obj1.setSubtitle("HAR测试商品"+no+"("+brand+")");
        obj1.setTitle("HAR测试商品"+no+"("+brand+")");
        obj1.setDesc("HAR测试商品"+no+"("+brand+")");
        obj1.setOuterId(new SimpleDateFormat("yyyyMMdd").format(new Date())+"_"+no);
        req.setLightPublishInfo(obj1);
        req.setOperateType("STORE");
        OmniitemItemPublishResponse rsp = client.execute(req, sessionKey);
        System.out.println(rsp.getBody());
    }


    @Test
    public void bandingImage() throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        OmniitemItemImageUploadRequest req = new OmniitemItemImageUploadRequest();

        //{"item_id":575370236869,"sku_ids":"{\"3947257483786\":\"HAR20180816_1\"}"}}  vivo
        req.setImg(new FileItem("C:\\Users\\gavin\\Desktop\\vivo.jpg"));
        req.setItemId(575370236869L);
//        req.set
//        {"item_id":575646478335,"sku_ids":"{\"3945621066538\":\"HAR20180816_2\"}"}} apple
//        req.setImg(new FileItem("C:\\Users\\gavin\\Desktop\\apple.jpg"));
//        req.setItemId(575646478335L);
        //{"item_id":575370968381,"sku_ids":"{\"3947262927717\":\"HAR20180816_3\"}"}} sansumg
//        req.setImg(new FileItem("C:\\Users\\gavin\\Desktop\\sansumg.jpg"));
//        req.setItemId(575370968381L);
        req.setMajor(true);
//        req.setPosition(0L);
        OmniitemItemImageUploadResponse rsp = client.execute(req, sessionKey);
        System.out.println(rsp.getBody());
    }
}
