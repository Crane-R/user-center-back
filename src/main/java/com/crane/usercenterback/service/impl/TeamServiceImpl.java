package com.crane.usercenterback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.mapper.TeamMapper;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.service.TeamService;
import org.springframework.stereotype.Service;

/**
* @author Crane Resigned
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-09-29 18:50:05
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




