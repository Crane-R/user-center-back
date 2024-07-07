package com.crane.usercenterback.service;

import com.crane.usercenterback.model.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void test() {
        User user = new User();
        user.setUsername("us");
        user.setNickName("s");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("s");
        user.setUserStatus(0);
        userService.save(user);

        StringUtils.isAnyBlank();
    }

}