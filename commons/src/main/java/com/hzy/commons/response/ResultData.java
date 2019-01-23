package com.hzy.commons.response;


import com.hzy.commons.utils.StrUtil;

public class ResultData {

    private Integer resultCode;
    private String resultMsg;
    private String errorMsg;
    private Object data;

    public ResultData(Integer resultCode, String resultMsg, String errorMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        if (!StrUtil.isBlank(errorMsg)) {
            this.errorMsg = errorMsg;
        }
    }

    public ResultData(Integer resultCode, String resultMsg, Object data) {
        this.resultCode = resultCode;
        this.errorMsg = resultMsg;
        if (!StrUtil.isEmpty(data)) {
            this.data = data;
        }
    }

    public ResultData() {
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer result_code) {
        this.resultCode = result_code;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String result_msg) {
        this.resultMsg = result_msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String error_msg) {
        this.errorMsg = error_msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
