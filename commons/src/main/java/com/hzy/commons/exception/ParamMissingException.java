package com.hzy.commons.exception;


import com.hzy.commons.constant.ResultTypeEnum;

public class ParamMissingException extends WxException {

    public ParamMissingException(String paramName) {
        super(ResultTypeEnum.MISS_PARAM, "缺少必填参数" + paramName);
    }

    public ParamMissingException(String paramName, String errMsg) {
        super(ResultTypeEnum.MISS_PARAM, paramName + errMsg);
    }
}
