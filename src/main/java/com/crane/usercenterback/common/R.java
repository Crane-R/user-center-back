package com.crane.usercenterback.common;

import com.sun.org.apache.xpath.internal.objects.XNull;
import lombok.Data;

/**
 * 统一返回体
 *
 * @Author Crane Resigned
 * @Date 2024/7/13 17:27:04
 */
@Data
public final class R {

    public static GeneralResponse<XNull> ok(String description) {
        return new GeneralResponse<>(SuccessStatus.SUCCESS.getCode(), null, SuccessStatus.SUCCESS.getMessage(), description);
    }

    public static <T> GeneralResponse<T> ok(T data) {
        return new GeneralResponse<>(SuccessStatus.SUCCESS.getCode(), data, SuccessStatus.SUCCESS.getMessage(), null);
    }

    public static <T> GeneralResponse<T> ok(T data, String description) {
        return new GeneralResponse<>(SuccessStatus.SUCCESS.getCode(), data, SuccessStatus.SUCCESS.getMessage(), description);
    }

    public static GeneralResponse<XNull> error(ErrorStatus status, String description) {
        return new GeneralResponse<>(status.getCode(), null, status.getMessage(), description);
    }

    public static <T> GeneralResponse<T> error(ErrorStatus status, T data, String description) {
        return new GeneralResponse<>(status.getCode(), data, status.getMessage(), description);
    }

}
