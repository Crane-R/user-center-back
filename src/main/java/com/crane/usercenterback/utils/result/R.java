package com.crane.usercenterback.utils.result;

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

    public static GeneralResponse<XNull> ok() {
        return new GeneralResponse<>("ok", null, null);
    }

    public static <T> GeneralResponse<T> ok(String msg, T data) {
        return new GeneralResponse<>("ok", msg, data);
    }

    public static <T> GeneralResponse<T> fails(String msg) {
        return new GeneralResponse<>("fails", msg, null);
    }
}
