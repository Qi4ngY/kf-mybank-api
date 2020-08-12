package com.mybank.exception;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/24
 */
public class CommonException extends StandardException {

	private static final long serialVersionUID = 1L;

	private String msg;

	public CommonException(String msg){
		super(msg);
	}

}
