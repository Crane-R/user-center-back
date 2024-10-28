package com.crane.usercenterback.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserDto;
import com.crane.usercenterback.model.domain.vo.UserVo;
import com.crane.usercenterback.model.dto.UserLoginRequest;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.common.GeneralResponse;
import com.crane.usercenterback.common.R;

import com.crane.usercenterback.utils.NullPointUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 用户控制层
 *
 * @Author Crane Resigned
 * @Date 2024/6/21 21:58:14
 */
@RestController()
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @PostMapping("/register")
    public GeneralResponse<Long> userRegister(@RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        String checkPassword = userDto.getCheckPassword();
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            return null;
        }
        return R.ok(userService.userRegister(userDto), "注册成功");
    }

    @PostMapping("/login")
    public GeneralResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        return R.ok(userService.userLogin(username, password, request), "登录成功");
    }

    /**
     * 根据用户名查询出一个集合的用户
     *
     * @Author CraneResigned
     * @Date 2024/7/7 11:09:22
     */
    @GetMapping("/query")
    public GeneralResponse<List<User>> userQuery(String username, HttpServletRequest request) {
        return R.ok(userService.userQuery(username, request));
    }

    /**
     * 前端登录时会调用此接口
     *
     * @Author CraneResigned
     * @Date 2024/7/14 14:01:13
     */
    @GetMapping("/current")
    public GeneralResponse<UserVo> userCurrent(HttpSession session) {
        return R.ok(userService.userCurrent(session));
    }

    /**
     * 退出接口
     *
     * @Author CraneResigned
     * @Date 2024/7/17 13:29:57
     */
    @PostMapping("/logout")
    public GeneralResponse userLogout(HttpSession session) {
        userService.userLogout(session);
        return R.ok("用户已注销");
    }

    /**
     * 删除
     *
     * @Author CraneResigned
     * @Date 2024/7/19 19:05:39
     */
    @PostMapping("/delete")
    public GeneralResponse<Boolean> deleteUser(@RequestBody String userId) {
        Boolean b = userService.userDelete(Long.parseLong(userId));
        if (b) {
            return R.ok(true, "删除成功");
        } else {
            return R.error(ErrorStatus.SYSTEM_ERROR, false, "删除失败");
        }
    }

    /**
     * 根据标签查询用户
     * a,b这样传就行
     *
     * @Author CraneResigned
     * @Date 2024/7/21 15:48:59
     */
    @GetMapping("/userQueryByTags")
    public GeneralResponse<List<UserVo>> userQueryByTags(String tagNames, Boolean isAnd, HttpSession session) {
        if (CharSequenceUtil.isBlank(tagNames)) {
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "标签不能为空");
        }
        String[] split = tagNames.split(",");
        return R.ok(userService.userQueryByTags(Arrays.asList(split), isAnd, session));
    }

    @PostMapping("/update")
    public GeneralResponse<Boolean> updateUser(@RequestBody User user, HttpSession session) {
        if (user == null) {
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "修改的用户不能为空");
        }
        User loginUser = userMapper.selectById(userCurrent(session).getData().getUserId());
        if (loginUser == null) {
            throw new BusinessException(ErrorStatus.NO_LOGIN);
        }
        return R.ok(userService.updateUser(user, loginUser), "用户已经被修改");
    }

    /**
     * 用户推荐首页
     *
     * @param pageSize 页尺寸
     * @param pageNum  页码
     * @param request
     * @Author CraneResigned
     * @Date 24/09/2024 13:13
     **/
    @GetMapping("/usersRecommend")
    public GeneralResponse<Page<UserVo>> usersRecommend(long pageSize, long pageNum, HttpServletRequest request) {
        return R.ok(userService.usersRecommend(pageSize, pageNum, request));
    }

    @GetMapping("/usersMatch")
    public GeneralResponse<List<UserVo>> usersMatch(Long num, HttpServletRequest request) {
        NullPointUtil.checkNullPoint(num);
        return R.ok(userService.usersMatch(num, request));
    }

}
