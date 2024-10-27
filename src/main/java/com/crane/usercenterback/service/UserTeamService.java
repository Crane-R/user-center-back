package com.crane.usercenterback.service;

import com.crane.usercenterback.model.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crane.usercenterback.model.dto.UserTeamAddDto;

/**
* @author Crane Resigned
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service
* @createDate 2024-09-29 18:54:50
*/
public interface UserTeamService extends IService<UserTeam> {

      Boolean userTeamAdd(UserTeamAddDto userTeamAddDto);

}
