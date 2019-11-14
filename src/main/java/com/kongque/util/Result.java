package com.kongque.util;

import com.kongque.constants.Constants;

public class Result <T> {

	private String returnCode = Constants.RESULT_CODE.SUCCESS;

	private String returnMsg = "操作成功";

	private T returnData;
	
	private Long total=0l;

	public Result() {

	}

	public Result(T data) {
		this.returnData = data;
	}
	
	public Result(T data,Long t) {
		this.returnData = data;
		this.total=t;
	}

	public Result(String returnCode, String returnMsg) {
		this.returnCode = returnCode;
		this.returnMsg = returnMsg;
	}

	public Result(String returnCode, String returnMsg, T returnData) {
		this.returnCode = returnCode;
		this.returnMsg = returnMsg;
		this.returnData = returnData;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public T getReturnData() {
		return returnData;
	}

	public void setReturnData(T returnData) {
		this.returnData = returnData;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
