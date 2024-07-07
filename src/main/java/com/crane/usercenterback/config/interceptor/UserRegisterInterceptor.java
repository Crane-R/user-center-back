package com.crane.usercenterback.config.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import com.crane.usercenterback.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验用户注册拦截器
 *
 * @Author Crane Resigned
 * @Date 2024/6/23 18:55:36
 */
@Slf4j
public class UserRegisterInterceptor implements HandlerInterceptor {

    /**
     * 拦截
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:20:36
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JSONObject jsonObject = DataUtil.getJsonObjByRequest(request);
        //判空
        String username = jsonObject.getStr("username");
        String nickname = jsonObject.getStr("nickname");
        String password = jsonObject.getStr("password");
        String checkPassword = jsonObject.getStr("checkPassword");
        if (CharSequenceUtil.hasBlank(username, nickname, password, checkPassword)) {
            log.error("传入的参数有空值");
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
