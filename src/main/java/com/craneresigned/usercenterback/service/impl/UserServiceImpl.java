package com.craneresigned.usercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craneresigned.usercenterback.model.domain.User;
import com.craneresigned.usercenterback.service.UserService;
import com.craneresigned.usercenterback.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-06-20 23:08:56
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long userRegister(String username, String nickName, String password, String checkPassword) {
        //判空处理
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            return -1L;
        }
        //密码不一致
        if (!password.equals(checkPassword)) {
            return -1L;
        }
        //校验是否含有特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            return -1L;
        }
        //账户名不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            return -1L;
        }
        User user = new User();
        user.setUsername(username);
        user.setNickName(StringUtils.isBlank(nickName) ? randomNickName() : nickName);
        //加密
        user.setUserPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        boolean save = this.save(user);
        if (!save) {
            return -1L;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String username, String password) {
        return null;
    }

    /**
     * 随机生成昵称
     * TODO：后面完善
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 21:51:34
     */
    private String randomNickName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 用户脱敏
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:44:08
     */
    private User getSafeUser(User user) {
        if (user == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setNickName(user.getNickName());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        return safeUser;
    }
}




