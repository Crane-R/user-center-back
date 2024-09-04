package com.crane.usercenterback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserDto;
import com.crane.usercenterback.model.domain.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-07-14 13:59:22
 */
public interface UserService extends IService<User> {

    /**
     * 注册功能，注册成功后返回该用户的ID
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:34:39
     */
    Long userRegister(UserDto userDto);

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

    /**
     * 用户注销
     *
     * @Author CraneResigned
     * @Date 2024/7/17 13:29:57
     */
    void userLogout(HttpSession session);

    /**
     * 删除用户
     *
     * @Author CraneResigned
     * @Date 2024/7/19 21:03:09
     */
    Boolean userDelete(Long userId);

    /**
     * 根据标签查询用户，根据一个状态符判断是or还是and
     *
     * @Author CraneResigned
     * @Date 2024/7/21 15:27:26
     */
    List<UserVo> userQueryByTags(List<String> tagNamesList, boolean isAnd);

}
