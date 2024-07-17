package com.crane.usercenterback.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用返回体
 *
 * @Author Crane Resigned
 * @Date 2024/7/13 17:37:52
 */
@AllArgsConstructor
@Data
public class GeneralResponse<T> {

    /**
     * 业务码
     *
     * @Author CraneResigned
     * @Date 2024/7/17 16:32:26
     */
    private Integer code;

    /**
     * 数据
     *
     * @Author CraneResigned
     * @Date 2024/7/17 16:32:55
     */
    private T data;

    /**
     * status枚举中的分类信息，如空值等
     *
     * @Author CraneResigned
     * @Date 2024/7/17 16:32:46
     */
    private String msg;

    /**
     * 详细描述，如为什么为空值
     *
     * @Author CraneResigned
     * @Date 2024/7/17 16:33:08
     */
    private String description;

}
