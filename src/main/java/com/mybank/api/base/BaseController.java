package com.mybank.api.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.api.request.dto.RequestEntity;
import com.mybank.api.request.dto.base.UploadFile;
import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.api.response.dto.base.UploadFileResponse;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.util.FileBase64ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/19
 */
@RestController
@RequestMapping("/api/base")
public class BaseController {

	@Autowired
	private RedisTemplate<String,String> template;

	@Value("${base_file_tmp_path}")
	private String tmpPath;

	@PostMapping("/uploadFile")
	@KfApi(method = KfApiMethodCode.KF_FILE_UPLOAD)
	public ResponseEntity<UploadFileResponse> uploadFile(RequestEntity<UploadFile> entity) throws Exception{
		String bizNo = UUID.randomUUID().toString();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(entity.getDto());
		template.opsForValue().set("kf:file:upload:" + bizNo,json,1, TimeUnit.HOURS);
		FileBase64ConvertUtil.decoderBase64File(entity.getDto().getContent(),tmpPath+ File.separator+bizNo+"."+entity.getDto().getFileSuffix());
		ResponseEntity<UploadFileResponse> responseEntity = new ResponseEntity<>();
		UploadFileResponse dto = new UploadFileResponse();
		dto.setBizNo(bizNo);
		responseEntity.setDto(dto);
		return responseEntity;
	}

}
