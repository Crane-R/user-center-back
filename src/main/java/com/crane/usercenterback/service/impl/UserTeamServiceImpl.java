package com.crane.usercenterback.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.constant.TeamConstants;
import com.crane.usercenterback.constant.TeamStatusEnum;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.TeamMapper;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.UserTeam;
import com.crane.usercenterback.model.dto.UserTeamAddDto;
import com.crane.usercenterback.service.TeamService;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.service.UserTeamService;
import com.crane.usercenterback.mapper.UserTeamMapper;
import com.crane.usercenterback.utils.NullPointUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    private final TeamMapper teamMapper;

    private final TeamService teamService;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Boolean userTeamAdd(UserTeamAddDto userTeamAddDto) {
        NullPointUtil.checkNullPoint(userTeamAddDto);

        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u_id", userTeamAddDto.getUserId());
        long count = super.count(queryWrapper);
        if (count >= TeamConstants.MAX_JOIN_NUM) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "用户加入队伍数量达到上线");
        }
        //检查队伍是否已满
        QueryWrapper<UserTeam> checkTeamUsersWrapper = new QueryWrapper<>();
        checkTeamUsersWrapper.eq("t_id", userTeamAddDto.getTeamId());
        long teamUsersCount = super.count(checkTeamUsersWrapper);
        if (teamUsersCount >= TeamConstants.MAX_TEAM_USERS) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍成员已满");
        }
        //检查队伍是否已经过期
        Team team = teamMapper.selectById(userTeamAddDto.getTeamId());
        if (team == null) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍不存在");
        }
        //如果是私有不给加入
        if (NumberUtil.equals(team.getTIsPublic(), TeamStatusEnum.PRIVATE.getCode())) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍私有不允许加入");
        }
        if (team.getExpiretime().before(new Date())) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍已过期");
        }
        //检查队伍队长是不是自己
        if (team.getTCaptainUId().equals(userTeamAddDto.getUserId()) && teamUsersCount != 0) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队长id");
        }
        //加密
        if (team.getTIsPublic().equals(TeamStatusEnum.ENCRYPT.getCode())
                && !SecureUtil.md5(userTeamAddDto.getPassword()).equals(team.getTPassword())) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍密码错误");
        }
        //加入队伍判重
        QueryWrapper<UserTeam> checkIsJoinWrapper = new QueryWrapper<>();
        checkIsJoinWrapper.eq("t_id", userTeamAddDto.getTeamId());
        checkIsJoinWrapper.eq("u_id", userTeamAddDto.getUserId());
        Long l = userTeamMapper.selectCount(checkIsJoinWrapper);
        if (l != 0) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "你已加入该队伍");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUId(userTeamAddDto.getUserId());
        userTeam.setTId(userTeamAddDto.getTeamId());
        userTeam.setJoinTime(userTeamAddDto.getJoinTime());
        int insert = userTeamMapper.insert(userTeam);
        if (insert != 1) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "添加失败，加入队伍失败");
        }
        //更新队伍信息
        teamService.teamUpdateTimeOnly(userTeamAddDto.getTeamId());
        return true;
    }
}




