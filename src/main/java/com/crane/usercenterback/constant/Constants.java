package com.crane.usercenterback.constant;

/**
 * 通用常量
 *
 * @Date 24/09/2024 18:28
 * @Author Crane Resigned
 */
public interface Constants {

    /**
     * 最大用户登录记录次数，到达该数字后会被立即减半
     * 为了防止溢出
     *
     * @Author CraneResigned
     * @Date 24/09/2024 18:30
     **/
    int MAX_UI_COUNT = 1000;

    /**
     * 首页推荐一页的数量
     *
     * @Author CraneResigned
     * @Date 2024/9/25 17:16
     **/
    int INDEX_PAGE_SIZE = 8;

}
