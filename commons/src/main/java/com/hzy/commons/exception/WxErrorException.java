package com.hzy.commons.exception;


import com.hzy.commons.constant.ResultTypeEnum;

public class WxErrorException extends WxException {

    public WxErrorException(String errMsg) {
        super(ResultTypeEnum.WX_ERROR, errMsg);
    }

    public WxErrorException() {
        super(ResultTypeEnum.WX_ERROR, "微信异常,请重试");
    }
}
