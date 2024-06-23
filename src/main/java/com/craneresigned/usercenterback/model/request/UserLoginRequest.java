package com.craneresigned.usercenterback.model.request;

import lombok.Data;

/**
 * 用户登录请求体
 *
 * @Author Crane Resigned
 * @Date 2024/6/23 17:39:10
 */
@Data
public class UserLoginRequest {

    private String username;

    private String password;

}