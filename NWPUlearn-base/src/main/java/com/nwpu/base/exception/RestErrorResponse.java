package com.nwpu.base.exception;

import java.io.Serializable;

/**
 * @author yfh
 * @version 1.0
 * @description 错误响应参数包装，以保证前端解析json时，参数名为errMessage
 * @date 2023/2/7
 */

public class RestErrorResponse implements Serializable {
    private String errMessage;
    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }
    public String getErrMessage() {
        return errMessage;
    }
    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
