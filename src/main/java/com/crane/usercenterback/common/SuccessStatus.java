package com.crane.usercenterback.common;

import lombok.Getter;

@Getter
public enum SuccessStatus {

    SUCCESS(20000, "成功");
    private final Integer code;

    private final String message;


    SuccessStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
