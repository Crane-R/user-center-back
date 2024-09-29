package com.crane.usercenterback.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long tId;

    /**
     * 队伍码，用于输入搜索队伍
     */
    private String tCode;

    /**
     * 队伍名称
     */
    private String tName;

    /**
     * 队伍描述
     */
    private String tDescription;

    /**
     * 最大人数
     */
    private Integer tMaxNum;

    /**
     * 过期时间
     */
    private Date expiretime;

    /**
     * 队伍队长用户id
     */
    private Long tCaptainUId;

    /**
     * 是否公开，0公开，1私密
     */
    private Integer tIsPublic;

    /**
     * 进入队伍密码
     */
    private String tPassword;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}