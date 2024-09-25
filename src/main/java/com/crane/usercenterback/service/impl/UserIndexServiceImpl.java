package com.crane.usercenterback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.model.domain.UserIndex;
import com.crane.usercenterback.service.UserIndexService;
import com.crane.usercenterback.mapper.UserIndexMapper;
import org.springframework.stereotype.Service;

/**
* @author Crane Resigned
* @description 针对表【user_index(用户索引表，用于缓存预热)】的数据库操作Service实现
* @createDate 2024-09-24 17:16:28
*/
@Service
public class UserIndexServiceImpl extends ServiceImpl<UserIndexMapper, UserIndex>
    implements UserIndexService{

}




