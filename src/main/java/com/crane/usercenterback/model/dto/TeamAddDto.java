package com.crane.usercenterback.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍添加请求体
 *
 * @Date 2024/10/26 10:58
 * @Author Crane Resigned
 */
@Data
public class TeamAddDto implements Serializable {

    private static final long serialVersionUID = -442823498795842916L;

    private String name;

    private String description;

    private Integer maxNum;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date expireTime;

    private Integer isPublic;

    private String password;

}
