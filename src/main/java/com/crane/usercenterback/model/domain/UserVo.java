package com.crane.usercenterback.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户前端展示对象
 *
 * @Author Crane Resigned
 * @Date 2024/9/4 22:52:53
 */
@Data
public class UserVo implements Serializable {

    private String avatarUrl;

    private Integer gender;

    private Long id;

    private String introduction;

    private String nickName;

    private List<String> tags;

    private Integer userRole;

    private Integer userStatus;

    private String username;

}
