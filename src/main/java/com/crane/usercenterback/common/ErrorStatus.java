package com.crane.usercenterback.common;

import lombok.Getter;

/**
 * 状态处理码枚举
 *
 * @Author CraneResigned
 * @Date 2024/7/17 16:14:54
 */
@Getter
public enum ErrorStatus {
    SYSTEM_ERROR(50000, "系统错误"),
    NULL_ERROR(50001, "空值异常"),
    BUSINESS_ERROR(50002, "业务错误"),
    NO_LOGIN(40000, "用户未登录"),
    USER_NULL(40001, "用户不存在"),
    PARAM_ERROR(40002, "参数错误"),
    NO_AUTHORITY(40003, "无权限");
    private final Integer code;

    private final String message;


    ErrorStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
