package com.craneresigned.usercenterback;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.craneresigned.usercenterback.mapper.UserMapper;
import com.craneresigned.usercenterback.model.domain.User;
import com.craneresigned.usercenterback.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.util.List;

@SpringBootTest
class UCBApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {

    }

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

    @Test
    void testPassDigest(){
        System.out.println(DigestUtils.md5DigestAsHex("password".getBytes()));

    }

    @Test
    void testSaveUser(){
        Long user01 = userService.userRegister("user01", null, "123", "123");
        System.out.println(user01);
    }

}
