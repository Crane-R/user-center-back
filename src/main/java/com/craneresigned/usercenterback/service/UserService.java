package com.craneresigned.usercenterback.service;

import com.craneresigned.usercenterback.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-06-20 23:08:56
 */
public interface UserService extends IService<User> {

    /**
     * 注册功能，注册成功后返回该用户的ID
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:34:39
     */
    Long userRegister(String username, String nickName, String password, String checkPassword);

    /**
     * 登录功能，返回的user是脱敏后的user
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:43:12
     */
    User userLogin(String username, String password);

}
