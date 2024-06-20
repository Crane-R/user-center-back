package com.craneresigned.usercenterback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craneresigned.usercenterback.model.domain.User;
import com.craneresigned.usercenterback.service.UserService;
import com.craneresigned.usercenterback.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author CraneResigned
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-06-20 23:08:56
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




