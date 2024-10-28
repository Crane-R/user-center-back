package com.crane.usercenterback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.vo.TeamVo;
import com.crane.usercenterback.model.dto.TeamAddDto;
import com.crane.usercenterback.model.dto.TeamQuery;
import com.crane.usercenterback.model.dto.TeamUpdateDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author Crane Resigned
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2024-09-29 18:50:05
 */
public interface TeamService extends IService<Team> {

    TeamVo teamAdd(TeamAddDto teamAddDto, HttpServletRequest request);

    Team teamDelete(Long id);

    Team teamUpdate(TeamUpdateDto teamUpdateDto);

    Team teamUpdateTimeOnly(Long teamId);

    List<Team> teamList();

    TeamVo selectById(Long id);

    Page<TeamVo> teamPage(TeamQuery teamQuery);

    TeamVo team2Vo(Team team);

    Team vo2Team(TeamVo teamVo);

    Boolean teamDisband(Long teamId, HttpServletRequest request);

    TeamVo teamQuit(Long teamId, HttpServletRequest request);

    List<TeamVo> teamListUserJoin(HttpServletRequest request);

}
