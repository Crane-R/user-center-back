package com.craneresigned.usercenterback.controller;

import com.craneresigned.usercenterback.model.request.UserRegisterRequest;
import com.craneresigned.usercenterback.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String test(){
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
        return userService.userRegister(username, userRegisterRequest.getNickName(), password, checkPassword);
    }

}
