package com.crane.usercenterback.service;

import com.crane.usercenterback.model.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crane.usercenterback.model.dto.UserTeamAddDto;

import javax.servlet.http.HttpServletRequest;

/**
* @author Crane Resigned
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service
* @createDate 2024-09-29 18:54:50
*/
public interface UserTeamService extends IService<UserTeam> {

      /**
       * 加入队伍
       *
       * @author CraneResigned
       * @date 2024/10/28 12:29
       **/
      Boolean userTeamAdd(UserTeamAddDto userTeamAddDto, HttpServletRequest request);

}
