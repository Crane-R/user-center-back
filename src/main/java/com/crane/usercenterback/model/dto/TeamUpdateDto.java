package com.crane.usercenterback.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改队伍信息请求体
 *
 * @Date 2024/10/27 17:41
 * @Author Crane Resigned
 */
@Data
public class TeamUpdateDto implements Serializable {

    private static final long serialVersionUID = -3007769564737344582L;

    private Long teamId;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 队伍队长用户id
     */
    private Long captainId;

    /**
     * 是否公开，0公开，1私密
     */
    private Integer isPublic;

    /**
     * 进入队伍密码
     */
    private String password;

}
