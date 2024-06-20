package com.craneresigned.usercenterback.model;

import lombok.Data;

/**
 * 用户类
 *
 * @Author Crane Resigned
 * @Date 2024/6/20 18:18:51
 */
@Data
public class User {

    private Long id;
    private String name;
    private Integer age;
    private String email;

}
