package com.hzy.commons.exception;

import com.hzy.commons.constant.ResultTypeEnum;

public class WxException extends RuntimeException {

    private ResultTypeEnum resultTypeEnum;
    private String resultMsg;

    public WxException(ResultTypeEnum resultCode, String errMsg) {
        super(errMsg);
        resultTypeEnum = resultCode;
        resultMsg = errMsg;
    }

    public ResultTypeEnum getResultTypeEnum() {
        return resultTypeEnum;
    }

    public String getResultMsg() {
        return resultMsg;
    }
}