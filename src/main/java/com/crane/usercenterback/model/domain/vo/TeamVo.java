package com.crane.usercenterback.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍vo，包含一个集合用户
 *
 * @Date 2024/10/27 14:49
 * @Author Crane Resigned
 */
@Data
public class TeamVo implements Serializable {

    private static final long serialVersionUID = 3018127589586526296L;

    private Long teamId;

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
    private String expireTime;

    /**
     * 队伍队长用户id
     */
    private Long captainId;

    /**
     * 是否公开，0公开，1私密
     */
    private Integer isPublic;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 入队用户列表
     *
     * @author CraneResigned
     * @date 2024/10/27 14:53
     **/
    private List<UserVo> userList;

}
