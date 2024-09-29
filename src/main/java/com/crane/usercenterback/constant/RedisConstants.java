package com.crane.usercenterback.constant;

/**
 * rediskeys
 *
 * @Date 24/09/2024 17:36
 * @Author Crane Resigned
 */
public interface RedisConstants {

    /**
     * 登录用户的key，使用的时候使用该模板
     *
     * @Author CraneResigned
     * @Date 24/09/2024 17:39
     **/
    String LOGIN_USER = "match:user:%s";

    /**
     * 缓存预热推荐redisKey，一个key对应一个用户的推荐信息
     *
     * @Author CraneResigned
     * @Date 2024/9/25 16:55
     **/
    String USER_RECOMMEND = "match:user:recommend:%s";

    /**
     * 分布式锁
     *
     * @Author CraneResigned
     * @Date 2024/9/29 11:30
     **/
    String DISTRIBUTED_LOCK = "distributed:lock:cache_warm";

}
