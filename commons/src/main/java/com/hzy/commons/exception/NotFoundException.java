package com.hzy.commons.exception;


import com.hzy.commons.constant.ResultTypeEnum;

public class NotFoundException extends WxException {

    public NotFoundException(String errMsg) {
        super(ResultTypeEnum.NOT_FOUND, errMsg);
    }
}
