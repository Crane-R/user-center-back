package com.crane.usercenterback.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @Author Crane Resigned
 * @Date 2024/6/20 18:24:24
 */
@RestController
public class TestController {

    @GetMapping("/")
    public String test(){
        return "Hello World";
    }

}
