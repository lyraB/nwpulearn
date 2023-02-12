package com.nwpu.base.exception;

import com.nwpu.base.exception.CommonError;
import com.nwpu.base.exception.NwpuException;
import com.nwpu.base.exception.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description 全局异常处理
 * @date 2023/2/8
 */
@Slf4j
@ControllerAdvice
@ResponseBody // 将信息返回为json
public class GlobalExceptionHandler {
    @ExceptionHandler(NwpuException.class) // 此方法捕获自定义NwpuException异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 需要返回状态码为500
    public RestErrorResponse customException(NwpuException e) {
        log.error("【捕获异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class) // 此方法捕获spring校验失败时抛出的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 需要返回状态码为500
    public RestErrorResponse doValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuffer errMsg = new StringBuffer();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors(); // 返回所有不合规参数
        fieldErrors.forEach(error -> {
            errMsg.append(error.getDefaultMessage()).append(",");
        });
        log.error(errMsg.toString());
        return new RestErrorResponse(errMsg.toString());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

}
