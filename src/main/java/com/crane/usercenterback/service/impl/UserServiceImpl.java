package com.crane.usercenterback.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.constant.Constants;
import com.crane.usercenterback.constant.RedisConstants;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.UserIndexMapper;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserDto;
import com.crane.usercenterback.model.domain.UserIndex;
import com.crane.usercenterback.model.domain.vo.UserVo;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.utils.AlgorithmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.crane.usercenterback.constant.UserConstants.MANAGER_STATUS;
import static com.crane.usercenterback.constant.UserConstants.USER_LOGIN_STATUS;

/**
 * @author CraneResigned
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-07-14 13:59:22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserIndexMapper userIndexMapper;

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

        //这里做一个缓存预热，如果redis中已经存在该用户，则直接匹配返回
        String redisKey = String.format(RedisConstants.LOGIN_USER, username);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        User user = (User) opsForValue.get(redisKey);
        //如果不为空则说明缓存预热命中
        if (user == null) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            queryWrapper.eq("user_password", DigestUtils.md5DigestAsHex(password.getBytes()));
            user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                log.error("用户名不存在");
                throw new BusinessException(ErrorStatus.USER_NULL, "用户名或密码错误");
            }
            log.info("缓存未命中");
            //加入缓存，过期时间半小时
            opsForValue.set(redisKey, user, 30, TimeUnit.MINUTES);
        }

        User safeUser = getSafeUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATUS, safeUser);

        //异步执行
        User finalUser = user;
        CompletableFuture.runAsync(() -> {
            //用户索引+1
            QueryWrapper<UserIndex> userIndexQueryWrapper = new QueryWrapper<>();
            userIndexQueryWrapper.eq("u_id", finalUser.getUserId());
            UserIndex userIndex = userIndexMapper.selectOne(userIndexQueryWrapper);
            if (userIndex == null) {
                userIndex = new UserIndex();
                userIndex.setUiCount(1);
                userIndex.setUId(finalUser.getUserId());
                userIndexMapper.insert(userIndex);
            } else {
                Integer uiCount = userIndex.getUiCount();
                userIndex.setUiCount((uiCount >= Constants.MAX_UI_COUNT ? uiCount / 2 : uiCount) + 1);
                userIndexMapper.updateById(userIndex);
            }
        });

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
     * 进入首页时，第一次肯定要查数据库的，后续就可以返回缓存了
     *
     * @param pageSize
     * @param pageNum
     * @param request
     * @Author CraneResigned
     * @Date 24/09/2024 13:14
     **/
    @Override
    public Page<UserVo> usersRecommend(long pageSize, long pageNum, HttpServletRequest request) {
        UserVo currentUserVo = userCurrent(request.getSession());
        String redisKey = String.format(RedisConstants.USER_RECOMMEND, currentUserVo.getUserId());
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Page<UserVo> userVoPage = (Page<UserVo>) opsForValue.get(redisKey);
        if (userVoPage != null) {
            return userVoPage;
        }
        //查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //todo:该queryWrapper需要增加对用户的推荐算法
        Page<User> page = super.page(new Page<>(pageNum, pageSize), queryWrapper);

        Page<UserVo> pageVo = userPage2UserVoPage(page);
        opsForValue.set(redisKey, pageVo, 30, TimeUnit.MINUTES);
        return pageVo;
    }

    @Override
    public Page<UserVo> userPage2UserVoPage(Page<User> userPage) {
        Page<UserVo> pageVo = new Page<>();
        BeanUtil.copyProperties(userPage, pageVo);
        //将pageVo里面的user转换为userVo
        List<UserVo> userVoList = new ArrayList<>();
        userPage.getRecords().forEach(user -> userVoList.add(user2Vo(user)));
        pageVo.setRecords(userVoList);
        return pageVo;
    }

    /**
     * 标签匹配
     * 这里将百万条数据存储在集合中，然后又存储在优先队列中，即使元素对象只有标签和userid，但也是十分消耗内存的
     * 两百万22秒左右，需要注意是我的电脑那么快的情况下22秒
     *
     * @author CraneResigned
     * @date 2024/10/28 18:59
     **/
    @Override
    public List<UserVo> usersMatch(Long num, HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        UserVo currentUser = userCurrent(request.getSession());
        if (currentUser == null) {
            throw new BusinessException(ErrorStatus.USER_NULL);
        }
        Long userId = currentUser.getUserId();
        String redisKey = String.format(RedisConstants.HEART_MATCH, userId);
        //如果缓存有就直接走缓存
        List<UserVo> cacheList = (List<UserVo>) redisTemplate.opsForValue().get(redisKey);
        if (cacheList != null) {
            return cacheList;
        }

        //这里是基于大数据查询思想，减少一些不必要的条件
        QueryWrapper<User> userListQueryWrapper = new QueryWrapper<>();
        userListQueryWrapper.select("user_id", "tags");
        userListQueryWrapper.isNotNull("tags");
        List<User> userList = this.list(userListQueryWrapper);
        List<String> tagList = currentUser.getTags();
        /*
         * key是userid，value是分数，分数越大代表相似度越差，
         * 优先队列默认按照最小堆存储，那么顶部就是相似度最好的
         * */
        PriorityQueue<Pair<Long, Long>> priorityQueue = new PriorityQueue<>((Comparator.comparing(Pair::getValue)));
        for (User tempUser : userList) {
            String tags = tempUser.getTags();
            //无标签或者当前用户为自己就跳过
            if (StrUtil.isBlank(tags) || Objects.equals(tempUser.getUserId(), userId)) {
                continue;
            }
            //计算分数
            long distance = AlgorithmUtil.minDistance(tagList, JSONUtil.parseArray(tags).toList(String.class));
            priorityQueue.add(new Pair<>(tempUser.getUserId(), distance));
        }
        //结束转换
        //优先队列的最小堆，从顶部开始取，取num个
        List<Long> userIdList = new ArrayList<>();
        while (num > 0) {
            Pair<Long, Long> poll = priorityQueue.poll();
            assert poll != null;
            userIdList.add(poll.getKey());
            num--;
        }
        //直接in ids这样查顺序会被打乱，只能一个一个查
        List<UserVo> resultList = new ArrayList<>();
        userIdList.forEach(id -> resultList.add(user2Vo(userMapper.selectById(id))));
        //将结果加入缓存
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(redisKey, resultList, 30, TimeUnit.MINUTES);
        stopWatch.stop();
        log.info("匹配算法共计耗时{}秒", stopWatch.getTotalTimeSeconds());
        return resultList;
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
    @Override
    public UserVo user2Vo(User user) {
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
        userVo.setCreateTime(user.getCreateTime());
        return userVo;
    }

    @Override
    public User vo2User(UserVo userVo) {
        User user = new User();
        user.setUserId(userVo.getUserId());
        user.setUsername(userVo.getUsername());
        user.setNickname(userVo.getNickname());
        user.setAvatarUrl(userVo.getAvatarUrl());
        user.setIntroduction(userVo.getIntroduction());
        user.setGender(userVo.getGender());
        user.setTags(JSONUtil.toJsonStr(userVo.getTags()));
        user.setUserRole(userVo.getUserRole());
        user.setUserStatus(userVo.getUserStatus());
        user.setCreateTime(userVo.getCreateTime());
        return null;
    }
}




