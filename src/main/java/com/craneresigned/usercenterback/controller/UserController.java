package com.craneresigned.usercenterback.controller;

import com.craneresigned.usercenterback.model.domain.User;
import com.craneresigned.usercenterback.model.request.UserLoginRequest;
import com.craneresigned.usercenterback.model.request.UserRegisterRequest;
import com.craneresigned.usercenterback.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            return null;
        }
        return userService.userRegister(username, userRegisterRequest.getNickname(), password, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        return userService.userLogin(username, password, request);
    }

    /**
     * 根据用户名查询出一个集合的用户
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:09:22
     */
    @GetMapping("/query")
    public List<User> userQuery(String username, HttpServletRequest request) {
        return userService.userQuery(username, request);
    }

}
