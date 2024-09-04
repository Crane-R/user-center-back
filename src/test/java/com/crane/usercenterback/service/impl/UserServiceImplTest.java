package com.crane.usercenterback.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.crane.usercenterback.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;


    @Test
    public void test(){
        String a = "[吉林市, 大同市, 莆田市]";
        JSON parse = JSONUtil.parseArray(a);
        System.out.println(parse);
    }

}