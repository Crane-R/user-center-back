package com.crane.usercenterback.config;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crane.usercenterback.constant.Constants;
import com.crane.usercenterback.constant.RedisConstants;
import com.crane.usercenterback.mapper.UserIndexMapper;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserIndex;
import com.crane.usercenterback.model.domain.vo.UserVo;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.service.impl.UserServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private UserService userService;

    @Autowired
    private RedissonClient redisson;

    /**
     * 缓存预热方法，
     * 在多个服务的情况下只有一个服务能够执行此方法
     * 分布式锁
     *
     * @Author CraneResigned
     * @Date 2024/9/29 12:44
     **/
    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        RLock lock = redisson.getLock(RedisConstants.DISTRIBUTED_LOCK);
        boolean isLock = lock.tryLock(1, -1, TimeUnit.SECONDS);
        if (!isLock) {
            log.info("获取锁失败");
            return;
        }

        try {
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
                String redisKey = String.format(RedisConstants.LOGIN_USER, u.getUsername());
                opsForValue.set(redisKey, u, 24, TimeUnit.HOURS);
                //缓存注入推荐
                QueryWrapper<User> recommendQueryWrapper = new QueryWrapper<>();
                Page<User> page = userService.page(new Page<>(1, Constants.INDEX_PAGE_SIZE), recommendQueryWrapper);
                Page<UserVo> userVoPage = UserServiceImpl.userPage2UserVoPage(page);
                String recommendRedisKey = String.format(RedisConstants.USER_RECOMMEND, u.getUserId());
                opsForValue.set(recommendRedisKey, userVoPage, 24, TimeUnit.HOURS);
            });

            stopWatch.stop();
            log.info("缓存预热总计耗时：{}毫秒", stopWatch.getTotalTimeMillis());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
