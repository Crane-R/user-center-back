package com.crane.usercenterback.controller;

import com.crane.usercenterback.common.GeneralResponse;
import com.crane.usercenterback.common.R;
import com.crane.usercenterback.model.dto.UserTeamAddDto;
import com.crane.usercenterback.service.UserTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户队伍关系接口
 *
 * @Date 2024/10/28 12:28
 * @Author Crane Resigned
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/userTeam")
public class UserTeamController {

    private final UserTeamService userTeamService;

    @PostMapping("/add")
    public GeneralResponse<Boolean> userTeamAdd(@RequestBody UserTeamAddDto userTeamAddDto, HttpServletRequest request) {
        return R.ok(userTeamService.userTeamAdd(userTeamAddDto,request));
    }

}
