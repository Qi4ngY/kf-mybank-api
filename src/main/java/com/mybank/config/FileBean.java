package com.mybank.config;

import com.alibaba.fastjson.JSONObject;
import com.mybank.util.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 上传文件处理
 */
@Component
public class FileBean {

    @Autowired
    private RedisUtils redisUtils;

    public String copyFileToProductDir(String key, String bizName, String paramName, String sourceDir, String targetDir){
        if(StringUtils.isEmpty(key)){
            return null;
        }
        String fileInfo = redisUtils.get("kf:file:upload:"+key);
        if(fileInfo == null || "".equals(fileInfo.trim())){
            throw new RuntimeException(paramName+"文件没有上传,或者以过期！");
        }
        JSONObject json = JSONObject.parseObject(fileInfo);
        if(!StringUtils.isEmpty(json.get("bizName")) && !json.getString("bizName").equals(bizName)){
            throw new RuntimeException("业务名称与上传文件对应的业务名称不匹配！");
        }

        if(!StringUtils.isEmpty(json.get("paramName")) && !json.getString("paramName").equals(paramName)){
            throw new RuntimeException("参数名称与上传文件对应的参数名称不匹配！");
        }

        try {
            long id = idWorker.nextId();
            FileCopyUtils.copy(new File(sourceDir +"/"+key+"."+json.getString("file_suffix")),
                    new File(targetDir + "/"+id+"."+json.getString("file_suffix")));
            return id+"."+json.getString("file_suffix");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static SnowflakeIdWorker idWorker = SnowflakeIdWorker.getSnowflakeIdWorker(SnowflakeIdWorker.DataCenter.FILE);
}
