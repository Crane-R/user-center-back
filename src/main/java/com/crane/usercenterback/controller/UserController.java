package com.crane.usercenterback.controller;

import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.request.UserLoginRequest;
import com.crane.usercenterback.model.request.UserRegisterRequest;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.utils.result.GeneralResponse;
import com.crane.usercenterback.utils.result.R;
import com.sun.org.apache.xpath.internal.objects.XNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户控制层
 *
 * @Author Crane Resigned
 * @Date 2024/6/21 21:58:14
 */
@RestController()
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @PostMapping("/register")
    public GeneralResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            return null;
        }
        return R.ok("注册成功", userService.userRegister(username, userRegisterRequest.getNickname(), password, checkPassword));
    }

    @PostMapping("/login")
    public GeneralResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        return R.ok("登录成功", userService.userLogin(username, password, request));
    }

    /**
     * 根据用户名查询出一个集合的用户
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:09:22
     */
    @GetMapping("/query")
    public GeneralResponse<List<User>> userQuery(String username, HttpServletRequest request) {
        return R.ok(null, userService.userQuery(username, request));
    }

    /**
     * 前端登录时会调用此接口
     *
     * @Author CraneResigned
     * @Date 2024/7/14 14:01:13
     */
    @GetMapping("/current")
    public GeneralResponse<User> userStatus(HttpSession session) {
        return R.ok(null, userService.userStatus(session));
    }

    /**
     * 退出接口
     *
     * @Author CraneResigned
     * @Date 2024/7/17 13:29:57
     */
    @PostMapping("/logout")
    public GeneralResponse<XNull> userLogout(HttpSession session) {
        userService.userLogout(session);
        return R.ok();
    }

}
