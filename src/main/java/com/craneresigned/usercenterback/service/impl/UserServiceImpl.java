package com.craneresigned.usercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craneresigned.usercenterback.model.domain.User;
import com.craneresigned.usercenterback.service.UserService;
import com.craneresigned.usercenterback.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-06-20 23:08:56
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录态常量
     *
     * @Author Crane Resigned
     * @Date 2024/6/23 17:37:46
     */
    private final String USER_LOGIN_STATUS = "USER_LOGIN_STATUS";

    /**
     * 管理员状态码，如果在数据库中用户是status字段值是1，则该用户是管理员
     *
     * @Author CraneResigned
     * @Date 2024/6/23 18:37:05
     */
    private final Integer MANAGER_STATUS = 1;

    /**
     * TODO：用户名应该是英文名才对，现在中文是允许通过的
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:14:40
     */
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
        user.setNickName(StringUtils.isBlank(nickName) ? username : nickName);
        //加密
        user.setUserPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        boolean save = this.save(user);
        if (!save) {
            return -1L;
        }
        log.info("注册成功");
        return user.getId();
    }

    @Override
    public User userLogin(String username, String password, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        //校验是否含有特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("user_password", DigestUtils.md5DigestAsHex(password.getBytes()));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        User safeUser = getSafeUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATUS, safeUser);
        log.info("登录成功");
        return safeUser;
    }

    /**
     * 用户查询功能，使用者是管理员
     * 调用此API的用户需要被鉴权，如果不是管理员就返回提示信息
     *
     * @Author CraneResigned
     * @Date 2024/7/7 10:57:42
     */
    @Override
    public List<User> userQuery(String username, HttpServletRequest request) {
        //用户鉴权
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            //这里要提示未登录，同下TODO
            log.error("用户未登录");
            return null;
        }
        if (!Objects.equals(user.getUserStatus(), MANAGER_STATUS)) {
            //TODO: 这里先返回null，后期搭建前端后统一配置返回体
            log.warn("该用户未具有管理员权限");
            return null;
        }
        //开始查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 用户脱敏
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:46:39
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




