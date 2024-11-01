package com.crane.usercenterback.model.dto;

import lombok.Data;

/**
 * 用户注册请求体
 *
 * @Author Crane Resigned
 * @Date 2024/6/21 22:02:06
 */
@Deprecated
@Data
public class UserRegisterRequest {

    private String username;

    private String nickname;

    private String password;

    private String checkPassword;

}
