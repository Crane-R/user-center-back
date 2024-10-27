package com.crane.usercenterback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crane.usercenterback.common.GeneralResponse;
import com.crane.usercenterback.common.R;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.dto.TeamAddDto;
import com.crane.usercenterback.model.dto.TeamQuery;
import com.crane.usercenterback.model.dto.TeamUpdateDto;
import com.crane.usercenterback.service.TeamService;
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

    @PostMapping("/add")
    public GeneralResponse<Team> teamAdd(@RequestBody TeamAddDto teamAddDto, HttpServletRequest request) {
        return R.ok(teamService.teamAdd(teamAddDto, request));
    }

    @PostMapping("/delete")
    public GeneralResponse<Team> teamDelete(Long teamId) {
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
    public GeneralResponse<Team> getTeamById(Long teamId) {
        return R.ok(teamService.selectById(teamId));
    }

    @PostMapping("/page")
    public GeneralResponse<Page<Team>> teamPage(@RequestBody TeamQuery teamQuery) {
        return R.ok(teamService.teamPage(teamQuery));
    }

}
