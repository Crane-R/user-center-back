package com.crane.usercenterback.exception;

import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.common.GeneralResponse;
import com.crane.usercenterback.common.R;
import com.sun.org.apache.xpath.internal.objects.XNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @Author Crane Resigned
 * @Date 2024/7/17 16:27:48
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理器
     *
     * @Author CraneResigned
     * @Date 2024/7/17 16:47:56
     */
    @ExceptionHandler(BusinessException.class)
    public GeneralResponse<XNull> businessExceptionHandle(BusinessException e) {
        log.error(e.getMessage(), e);
        return R.error(e.getStatus(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public GeneralResponse<XNull> runtimeExceptionHandle(RuntimeException e) {
        log.error(e.getMessage(), e);
        return R.error(ErrorStatus.SYSTEM_ERROR, e.getMessage());
    }

}
