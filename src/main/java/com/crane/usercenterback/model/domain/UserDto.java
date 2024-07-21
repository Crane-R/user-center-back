package com.crane.usercenterback.model.domain;

import lombok.Data;

/**
 * 用户传输对象，用于控制层传入用户数据到业务层
 *
 * @Author Crane Resigned
 * @Date 2024/7/19 15:35:28
 */
@Data
public class UserDto {

    /**
     * 用户名，用于登录
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户确认密码
     *
     * @Author CraneResigned
     * @Date 2024/7/19 15:37:48
     */
    private String CheckPassword;

    /**
     * 性别
     */
    private Integer gender = 2;

}