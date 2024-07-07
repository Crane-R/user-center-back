package com.craneresigned.usercenterback.config.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import com.craneresigned.usercenterback.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录拦截器
 *
 * @Author Crane Resigned
 * @Date 2024/7/7 11:26:41
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JSONObject json = DataUtil.getJsonObjByRequest(request);
        String username = json.getStr("username");
        String password = json.getStr("password");
        if (CharSequenceUtil.hasBlank(username,password)){
            log.error("用户登录传入的用户名或密码为空");
            return false;
        }
        //特殊字符处理
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            log.error("传入的参数有特殊字符");
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
