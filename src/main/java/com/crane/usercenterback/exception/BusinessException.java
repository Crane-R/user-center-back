package com.crane.usercenterback.exception;

import com.crane.usercenterback.common.ErrorStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @Author Crane Resigned
 * @Date 2024/7/17 16:19:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private final ErrorStatus status;

    private final String description;

    public BusinessException(ErrorStatus status, String description) {
        this.status = status;
        this.description = description;
    }

    public BusinessException(ErrorStatus status) {
        this.status = status;
        this.description = "";
    }

}
