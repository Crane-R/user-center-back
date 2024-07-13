package com.crane.usercenterback.utils.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.ConstructorArgs;

/**
 * 通用返回体
 *
 * @Author Crane Resigned
 * @Date 2024/7/13 17:37:52
 */
@AllArgsConstructor
@Data
public class GeneralResponse<T> {

    private String status;

    private String msg;

    private T data;

}
