package com.crane.usercenterback.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 数据获取工具
 *
 * @Author Crane Resigned
 * @Date 2024/7/7 11:23:07
 */
public final class DataUtil {

    /**
     * 获取请求体的参数
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:25:09
     */
    public static JSONObject getJsonObjByRequest(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        reader.close();
        return JSONUtil.parseObj(jsonBuilder);
    }

}
