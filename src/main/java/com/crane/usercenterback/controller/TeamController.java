package com.crane.usercenterback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crane.usercenterback.common.GeneralResponse;
import com.crane.usercenterback.common.R;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.vo.TeamVo;
import com.crane.usercenterback.model.dto.TeamAddDto;
import com.crane.usercenterback.model.dto.TeamIdDto;
import com.crane.usercenterback.model.dto.TeamQuery;
import com.crane.usercenterback.model.dto.TeamUpdateDto;
import com.crane.usercenterback.service.TeamService;
import com.crane.usercenterback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 *
 * @Date 2024/10/26 11:35
 * @Author Crane Resigned
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    private final UserService userService;

    @PostMapping("/add")
    public GeneralResponse<TeamVo> teamAdd(@RequestBody TeamAddDto teamAddDto, HttpServletRequest request) {
        return R.ok(teamService.teamAdd(teamAddDto, request));
    }

    @PostMapping("/delete")
    public GeneralResponse<Team> teamDelete(@RequestBody Long teamId) {
        return R.ok(teamService.teamDelete(teamId));
    }

    @PostMapping("/update")
    public GeneralResponse<Team> teamUpdate(@RequestBody TeamUpdateDto teamUpdateDto) {
        return R.ok(teamService.teamUpdate(teamUpdateDto));
    }

    @GetMapping("/list")
    public GeneralResponse<List<Team>> teamList() {
        return R.ok(teamService.teamList());
    }

    @GetMapping("/selectOne")
    public GeneralResponse<TeamVo> getTeamById(Long teamId) {
        return R.ok(teamService.selectById(teamId));
    }

    @PostMapping("/page")
    public GeneralResponse<Page<TeamVo>> teamPage(@RequestBody TeamQuery teamQuery) {
        return R.ok(teamService.teamPage(teamQuery));
    }

    @PostMapping("/disband")
    public GeneralResponse<Boolean> teamDisband(@RequestBody TeamIdDto teamIdDto, HttpServletRequest request) {
        return R.ok(teamService.teamDisband(teamIdDto.getTeamId(), request));
    }

    @PostMapping("/quit")
    public GeneralResponse<TeamVo> teamQuit(@RequestBody TeamIdDto teamIdDto, HttpServletRequest request) {
        return R.ok(teamService.teamQuit(teamIdDto.getTeamId(), request));
    }

    /**
     * 获取当前用户创建的队伍
     *
     * @author CraneResigned
     * @date 2024/10/28 14:32
     **/
    @GetMapping("/teamListBySelfCreate")
    public GeneralResponse<Page<TeamVo>> teamListBySelfCreate(HttpServletRequest request) {
        Long userId = userService.userCurrent(request.getSession()).getUserId();
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setCaptainId(userId);
        return teamPage(teamQuery);
    }

    /**
     * 获取当前用户加入的队伍
     *
     * @author CraneResigned
     * @date 2024/10/28 14:51
     **/
    @GetMapping("/teamListUserJoin")
    public GeneralResponse<List<TeamVo>> teamListUserJoin(HttpServletRequest request) {
        return R.ok(teamService.teamListUserJoin(request));
    }

}
