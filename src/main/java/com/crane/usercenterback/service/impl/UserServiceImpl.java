package com.crane.usercenterback.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crane.usercenterback.constant.UserConstant.MANAGER_STATUS;
import static com.crane.usercenterback.constant.UserConstant.USER_LOGIN_STATUS;
import static java.util.Arrays.stream;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-07-14 13:59:22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;


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
            log.error("用户名、密码、确认密码都不能为空");
            return -1L;
        }
        //密码不一致
        if (!password.equals(checkPassword)) {
            log.error("密码与确认密码不一致");
            return -1L;
        }
        //校验是否含有特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            log.error("用户名不能含有特殊字符");
            return -1L;
        }
        //账户名不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            log.error("用户名重复，请重新输入");
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
            log.error("用户名含有特殊字符");
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("user_password", DigestUtils.md5DigestAsHex(password.getBytes()));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error("用户名不存在");
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
        if (!Objects.equals(user.getUserRole(), MANAGER_STATUS)) {
            log.warn("该用户未具有管理员权限");
            return null;
        }
        //开始查询用户
        List<User> userList;
        if (CharSequenceUtil.isEmpty(username)) {
            userList = userMapper.selectList(null);
        } else {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("username", username);
            userList = userMapper.selectList(queryWrapper);
        }
        //TODO:这里能否使用流来实现？
        List<User> safeUserList = new ArrayList<>();
        userList.forEach(u -> safeUserList.add(getSafeUser(u)));
        return safeUserList;
    }

    @Override
    public User userStatus(HttpSession session) {
        User user = (User) session.getAttribute(USER_LOGIN_STATUS);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", user.getId());
        return getSafeUser(userMapper.selectOne(queryWrapper));
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
        safeUser.setUserRole(user.getUserRole());
        return safeUser;
    }
}




