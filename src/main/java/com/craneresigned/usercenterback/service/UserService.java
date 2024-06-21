package com.craneresigned.usercenterback.service;

import com.craneresigned.usercenterback.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-06-20 23:08:56
 */
public interface UserService extends IService<User> {

    Long userRegister(String username, String nickName, String password, String checkPassword);

}
