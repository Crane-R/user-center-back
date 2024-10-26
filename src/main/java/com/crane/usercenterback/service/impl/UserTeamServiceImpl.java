package com.crane.usercenterback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.model.domain.UserTeam;
import com.crane.usercenterback.service.UserTeamService;
import com.crane.usercenterback.mapper.UserTeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Crane Resigned
 * @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
 * @createDate 2024-09-29 18:54:50
 */
@Service
@RequiredArgsConstructor
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
        implements UserTeamService {

    private final UserTeamMapper userTeamMapper;

    @Override
    public Boolean userTeamAdd(UserTeam userTeam) {
        return userTeamMapper.insert(userTeam) == 1;
    }
}




