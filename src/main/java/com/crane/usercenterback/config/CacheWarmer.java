package com.crane.usercenterback.config;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crane.usercenterback.constant.RedisConstants;
import com.crane.usercenterback.mapper.UserIndexMapper;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserIndex;
import com.crane.usercenterback.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存预热
 * 注意：缓存预热存储的是用户
 *
 * @Date 24/09/2024 17:28
 * @Author Crane Resigned
 */
@Component
@Slf4j
public class CacheWarmer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserIndexMapper userIndexMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        QueryWrapper<UserIndex> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ui_count").last("limit 20");
        List<UserIndex> userList = userIndexMapper.selectList(wrapper);
        //如果用户索引列表为空，则不用预热直接返回
        if (userList == null || userList.isEmpty()) {
            stopWatch.stop();
            return;
        }
        List<Long> userIds = new ArrayList<>();
        userList.forEach(e -> userIds.add(e.getUId()));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("user_id", userIds);
        List<User> users = userMapper.selectList(userQueryWrapper);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        users.forEach(u -> {
            String redisKey = String.format(RedisConstants.LOGIN_KEY_TEMPLATE, u.getUsername());
            opsForValue.set(redisKey, u);
        });

        stopWatch.stop();
        log.info("缓存预热总计耗时：{}毫秒", stopWatch.getTotalTimeMillis());
    }

}
