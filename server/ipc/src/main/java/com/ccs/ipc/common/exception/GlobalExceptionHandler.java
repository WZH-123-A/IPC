package com.ccs.ipc.common.exception;

import com.ccs.ipc.common.response.Response;
import com.ccs.ipc.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Response> exceptionHandler(ApiException e) {
        Response response = Response.fail(e.getMessage());
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidateException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        Response<Map<String, String>> response = Response.fail(ResultCode.PARAM_VALIDATE_ERROR, errors);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {} - {}", e.getMethod(), e.getMessage());
        Response response = Response.fail(ResultCode.METHOD_NOT_ALLOWED, null);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        log.error("系统异常: ", e);
        Response response = Response.fail(ResultCode.FAIL, null);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
