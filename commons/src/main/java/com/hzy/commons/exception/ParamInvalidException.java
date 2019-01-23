package com.hzy.commons.exception;


import com.hzy.commons.constant.ResultTypeEnum;

public class ParamInvalidException extends WxException {

    public ParamInvalidException() {
        super(ResultTypeEnum.PARAM_INVALID, "参数错误,请检查是否符合要求");
    }

    public ParamInvalidException(String errMsg) {
        super(ResultTypeEnum.PARAM_INVALID, errMsg);
    }

    public ParamInvalidException(String paramName, String errMsg) {
        super(ResultTypeEnum.PARAM_INVALID, paramName + " "  + errMsg);
    }
}
