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

    private Long userId;

    private String username;

    private String nickname;

    private String avatarUrl;

    private String introduction;

    private Integer gender;

    private List<String> tags;

    private Integer userRole;

    private Integer userStatus;

    private String createTime;
}
