package com.crane.usercenterback.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍查询封装体
 *
 * @Date 2024/10/26 11:28
 * @Author Crane Resigned
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageDto implements Serializable {

    private static final long serialVersionUID = -4389586769089120105L;
    /**
     * 队伍码，用于输入搜索队伍
     */
    private String code;

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
    private Date expireTime;

    /**
     * 队伍队长用户id
     */
    private Long captainId;

    /**
     * 是否公开，0公开，1私密
     */
    private Integer isPublic;

}
