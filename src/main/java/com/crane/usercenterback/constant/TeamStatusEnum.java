package com.crane.usercenterback.constant;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 队伍状态枚举
 *
 * @Date 2024/10/27 17:59
 * @Author Crane Resigned
 */
@AllArgsConstructor
@Getter
public enum TeamStatusEnum {

    PUBLIC(0, "公开"),

    /**
     * 私有要密码，公开不要
     *
     * @author CraneResigned
     * @date 2024/10/27 18:01
     **/
    PRIVATE(1, "私有"),

    ENCRYPT(2, "加密");

    private final Integer code;

    private final String description;


}
