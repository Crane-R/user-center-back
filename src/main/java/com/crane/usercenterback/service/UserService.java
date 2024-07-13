package com.crane.usercenterback.service;

import com.crane.usercenterback.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crane.usercenterback.utils.result.GeneralResponse;
import com.crane.usercenterback.utils.result.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

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
    User userLogin(String username, String password, HttpServletRequest request);

    /**
     * 这是一个管理员功能，调用此方法时需要用户鉴权
     * 用户查询，根据用户名查询用户
     *
     * @Author CraneResigned
     * @Date 2024/6/23 18:30:40
     */
    List<User> userQuery(String username, HttpServletRequest request);

    /**
     * 获取用户登录态
     *
     * @Author CraneResigned
     * @Date 2024/7/13 18:56:46
     */
    User userStatus(HttpSession session);
}
