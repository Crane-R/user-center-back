package com.crane.usercenterback.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 用户加入队伍请求体
 *
 * @Date 2024/10/27 18:35
 * @Author Crane Resigned
 */
@Data
public class UserTeamAddDto {

    private Long userId;

    private Long teamId;

    private Date joinTime;

    private String password;

}
