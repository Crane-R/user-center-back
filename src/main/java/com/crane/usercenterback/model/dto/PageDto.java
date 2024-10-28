package com.crane.usercenterback.model.dto;

import com.crane.usercenterback.constant.TeamConstants;
import lombok.Data;

/**
 * 分页请求体
 *
 * @Date 2024/10/26 11:25
 * @Author Crane Resigned
 */
@Data
public class PageDto {

    protected int pageSize = TeamConstants.PAGE_SIZE;

    protected int pageNum = TeamConstants.PAGE_NUMBER;

}
