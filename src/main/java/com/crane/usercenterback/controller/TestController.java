package com.crane.usercenterback.controller;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.UUID;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 测试
 *
 * @Author Crane Resigned
 * @Date 2024/6/20 18:24:24
 */
@RestController()
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String test() {
        return "Hello World";
    }


    /**
     * 批量增加用户
     *
     * @param size 总插入数
     * @Author CraneResigned
     * @Date 22/09/2024 18:35
     * 批量插入数量：100000
     * 批量插入耗时：17678ms
     * <p>
     * 批量插入数量：100000
     * 批量插入耗时：4106ms
     * <p>
     * 批量插入数量：100000
     * 批量插入耗时：2993ms
     * <p>
     * 批量插入数量：1000000
     * 批量插入耗时：29450ms
     **/
    @PostMapping("/batchInsert")
    public void batchInsertUsers(@RequestBody Integer size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int unit = 10000;
        int time = size / unit;

        //如果小于1w，就不走多线程
        if (time <= 0) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                users.add(getNewUser());
            }
            userService.saveBatch(users);
        } else {
            int j = 0;
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            for (int i = 0; i < time; i++) {
                List<User> users = new ArrayList<>();
                do {
                    j++;
                    users.add(getNewUser());
                } while (j % unit != 0);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> userService.saveBatch(users, unit));
                futureList.add(future);
            }
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        }

        stopWatch.stop();
        System.out.println("批量插入数量：" + size);
        System.out.println("批量插入耗时：" + stopWatch.getLastTaskTimeMillis() + "ms");
    }

    private User getNewUser() {
        User user = new User();
        String name = UUID.randomUUID().toString();
        user.setUsername(name);
        user.setNickname(name);
        user.setAvatarUrl("https://img.zcool.cn/community/01c13956d1bf7132f875520feb6244.jpg@2o.jpg");
        user.setGender(1);
        user.setUserPassword("123456");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setTags("[西汉市, 海口市, 包原市]");
        user.setIntroduction("介绍文本");
        return user;
    }

}
