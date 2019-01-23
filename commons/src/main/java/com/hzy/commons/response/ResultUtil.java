package com.hzy.commons.response;

import com.hzy.commons.constant.ResultTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResultUtil {

    public static ResponseEntity<ResultData> success(Object data) {
        ResultData resultData = new ResultData();
        resultData.setResultCode(ResultTypeEnum.OK.getResultCode());
        resultData.setResultMsg(ResultTypeEnum.OK.name());
        if (null != data) {
            resultData.setData(data);
        }
        return new ResponseEntity<>(resultData, HttpStatus.OK);
    }

    public static ResponseEntity<ResultData> success(Object data, String resultMsg) {
        ResultData resultData = new ResultData();
        resultData.setResultCode(ResultTypeEnum.OK.getResultCode());
        resultData.setResultMsg(resultMsg);
        if (null != data) {
            resultData.setData(data);
        }
        return new ResponseEntity<>(resultData, HttpStatus.OK);
    }
}
