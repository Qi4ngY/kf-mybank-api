package com.mybank.api.base;

import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.exception.CommonException;
import com.mybank.exception.StandardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity handlerException(Exception e, HttpServletRequest request) {

		ResponseEntity entity = new ResponseEntity();
		if (ThreadLocalParams.getInstance().getEntity() != null) {
			entity.setBizContent(ThreadLocalParams.getInstance().getEntity().getBizContent());
		}
		if (e instanceof CommonException||e instanceof StandardException) {
			entity.setResponseCode("40000");
			entity.setMsg(e.getMessage());
		} else if(e instanceof MethodArgumentNotValidException){
			MethodArgumentNotValidException e1 = (MethodArgumentNotValidException) e;
			entity.setResponseCode("10001");
			FieldError fieldError = e1.getBindingResult().getFieldError();
			entity.setMsg("参数异常:" + (fieldError != null ? fieldError.getDefaultMessage() : "未捕获的参数异常信息"));
		} else {
			logger.error("系统异常:"+request.getRequestURI(), e);
			entity.setResponseCode("40004");
			entity.setMsg("系统异常");
		}
		String ip = request.getHeader("x-forwarded-for") == null ? request.getRemoteAddr()
				: request.getHeader("x-forwarded-for");
		logger.info("ThreadId-{}:响应码:{},响应消息:{},请求URL:{},请求IP:{}",
				Thread.currentThread().getId(), entity.getResponseCode(), entity.getMsg(),
				request.getRequestURI(),ip);
		logger.info("--------------异常返回清空线程参数----------------");
		ThreadLocalParams.getInstance().clear();
		return entity;
	}





}
