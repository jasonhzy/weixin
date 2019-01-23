package com.hzy.commons.constant;

public enum ResultTypeEnum {
    OK(0),
    FAIL(1),
    NOT_FOUND(10),
    WX_ERROR(20),
    MISS_PARAM(30),
    PARAM_INVALID(40),
    ;

    private int resultCode;

    ResultTypeEnum(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}
