package com.crane.usercenterback.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserDto;
import com.crane.usercenterback.model.domain.UserVo;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.crane.usercenterback.constant.UserConstant.MANAGER_STATUS;
import static com.crane.usercenterback.constant.UserConstant.USER_LOGIN_STATUS;

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
    public Long userRegister(UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        String checkPassword = userDto.getCheckPassword();
        String nickName = userDto.getNickname();
        Integer gender = userDto.getGender();

        //判空处理
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            log.error("用户名、密码、确认密码都不能为空");
            throw new BusinessException(ErrorStatus.NULL_ERROR, "用户名、密码、确认密码都不能为空");
        }
        //密码不一致
        if (!password.equals(checkPassword)) {
            log.error("密码与确认密码不一致");
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "密码与确认密码不一致");
        }
        //校验是否含有特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            log.error("用户名不能含有特殊字符");
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "用户名含有特殊字符");
        }

        //判断标签是否为空
        String[] tagNames = userDto.getTagNames();
        if (CharSequenceUtil.isAllBlank(tagNames)) {
            log.error("标签组合不能为空");
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "新注册用户至少要有一个标签");
        }

        //账户名不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            log.error("用户名重复，请重新输入");
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "用户名重复");
        }
        User user = new User();
        user.setUsername(username);
        user.setNickname(StringUtils.isBlank(nickName) ? username : nickName);
        //加密
        user.setUserPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setGender(gender);
        user.setTags(Arrays.toString(tagNames));
        user.setIntroduction(userDto.getIntroduction());
        user.setUserRole(userDto.getUserRole());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "用户新增失败");
        }
        log.info("注册成功");
        return user.getUserId();
    }

    @Override
    public User userLogin(String username, String password, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorStatus.NULL_ERROR, "用户名或密码为空");
        }
        //校验是否含有特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        if (username.matches(regEx)) {
            log.error("用户名含有特殊字符");
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "用户名含有特殊字符");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("user_password", DigestUtils.md5DigestAsHex(password.getBytes()));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error("用户名不存在");
            throw new BusinessException(ErrorStatus.USER_NULL, "用户名或密码错误");
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
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            //这里要提示未登录，同下TODO
            log.error("用户未登录");
            throw new BusinessException(ErrorStatus.NO_LOGIN, null);
        }
        //用户鉴权，这里重新查询一次用户信息以保证数据实效性
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", user.getUserId());
        user = userMapper.selectOne(userQueryWrapper);
        if (!Objects.equals(user.getUserRole(), MANAGER_STATUS)) {
            log.warn("该用户未具有管理员权限");
            throw new BusinessException(ErrorStatus.NO_AUTHORITY, "该用户未具有管理员权限");
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
        Collections.reverse(safeUserList);
        return safeUserList;
    }

    @Override
    public UserVo userCurrent(HttpSession session) {
        User user = (User) session.getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(ErrorStatus.NO_LOGIN, "请先登录");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getUserId());
        return user2Vo(userMapper.selectOne(queryWrapper));
    }

    /**
     * 用户脱敏
     *
     * @Author Crane Resigned
     * @Date 2024/6/21 22:46:39
     */
    private User getSafeUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorStatus.NULL_ERROR, "脱敏的源用户为空");
        }
        User safeUser = new User();
        safeUser.setUserId(user.getUserId());
        safeUser.setUsername(user.getUsername());
        safeUser.setNickname(user.getNickname());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        safeUser.setUserRole(user.getUserRole());
        return safeUser;
    }

    @Override
    public void userLogout(HttpSession session) {
        session.removeAttribute(USER_LOGIN_STATUS);
    }

    @Override
    public Boolean userDelete(Long userId) {
        int i = userMapper.deleteById(userId);
        return i > 0;
    }

    @Override
    public List<UserVo> userQueryByTags(List<String> tagNamesList, boolean isAnd, HttpSession session) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("user_id", userCurrent(session).getUserId());
        if (isAnd) {
            queryWrapper.and(sub -> tagNamesList.forEach(tag -> sub.like("tags", tag)));
        } else {
            queryWrapper.and(sub -> tagNamesList.forEach(tag -> sub.or(innerWrapper -> innerWrapper.like("tags", tag))));
        }

        //将user转换为userVo
        List<UserVo> resultList = new ArrayList<>();
        userMapper.selectList(queryWrapper).forEach(e -> resultList.add(user2Vo(e)));
        return resultList;
    }

    /**
     * 修改用户，只有管理员和用户自己能够修改
     *
     * @param user      要修改的用户
     * @param loginUser 登录用户
     * @return 返回是否成功
     */
    @Override
    public boolean updateUser(User user, User loginUser) {
        Long id = user.getUserId();
        if (id == null || id < 0) {
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "ID错误");
        }
        //如果进来的用户不是管理员或者不是用户自己就抛出异常
        if (!isAdmin(loginUser) && !Objects.equals(id, loginUser.getUserId())) {
            throw new BusinessException(ErrorStatus.NO_AUTHORITY, "无权修改");
        }
        User updatedUser = userMapper.selectById(id);
        if (updatedUser == null) {
            throw new BusinessException(ErrorStatus.USER_NULL);
        }
        if (StrUtil.isNotBlank(user.getAvatarUrl())) {
            updatedUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (StrUtil.isNotBlank(user.getNickname())) {
            updatedUser.setNickname(user.getNickname());
        }
        if (StrUtil.isNotBlank(user.getIntroduction())) {
            updatedUser.setIntroduction(user.getIntroduction());
        }
        if (user.getGender() != null) {
            updatedUser.setGender(user.getGender());
        }
        if (StrUtil.isNotBlank(user.getTags())) {
            updatedUser.setTags(user.getTags());
        }
        return userMapper.updateById(updatedUser) == 1;
    }

    /**
     * 判断某个用户是否是管理员
     *
     * @param user
     * @return
     */
    private boolean isAdmin(User user) {
        return user != null && Objects.equals(user.getUserRole(), MANAGER_STATUS);
    }

    /**
     * 传入user返回vo
     *
     * @param user
     * @Author Crane Resigned
     * @Date 21/09/2024 13:26
     **/
    private UserVo user2Vo(User user) {
        UserVo userVo = new UserVo();
        userVo.setAvatarUrl(user.getAvatarUrl());
        Integer gender = user.getGender();
        userVo.setGender(gender);
        userVo.setUserId(user.getUserId());
        userVo.setIntroduction(user.getIntroduction());
        userVo.setNickname(user.getNickname());
        String tags = user.getTags();
        if (StrUtil.isNotBlank(tags)) {
            userVo.setTags(JSONUtil.parseArray(tags).toList(String.class));
        }
        userVo.setUserRole(user.getUserRole());
        userVo.setUserStatus(user.getUserStatus());
        userVo.setUsername(user.getUsername());
        userVo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getCreateTime()));
        return userVo;
    }

}




