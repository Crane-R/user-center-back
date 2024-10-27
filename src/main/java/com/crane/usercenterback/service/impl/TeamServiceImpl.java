package com.crane.usercenterback.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.constant.TeamStatusEnum;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.TeamMapper;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserTeam;
import com.crane.usercenterback.model.dto.PageDto;
import com.crane.usercenterback.model.dto.TeamAddDto;
import com.crane.usercenterback.model.dto.TeamQuery;
import com.crane.usercenterback.model.dto.TeamUpdateDto;
import com.crane.usercenterback.service.TeamService;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.service.UserTeamService;
import com.crane.usercenterback.utils.NullPointUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author Crane Resigned
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2024-09-29 18:50:05
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    private final TeamMapper teamMapper;

    private final UserService userService;

    private final UserTeamService userTeamService;

    private final UserMapper userMapper;

    /**
     * 事务隔离级别读已提交，避免脏读
     * （即我这里这个方法还没有提交的时候，别的事务是读不到的）
     *
     * @author CraneResigned
     * @date 2024/10/26 12:13
     **/
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Team teamAdd(TeamAddDto teamAddDto, HttpServletRequest request) {
        NullPointUtil.checkNullPoint("添加队伍的参数不能为空", teamAddDto);
        Long userId = userService.userCurrent(request.getSession()).getUserId();
        Team team = new Team();
        team.setTCode(RandomUtil.randomString(6));
        team.setTName(teamAddDto.getName());
        team.setTDescription(teamAddDto.getDescription());
        team.setTMaxNum(teamAddDto.getMaxNum());
        //todo:这里过期时间是要优化处理的

        team.setExpiretime(teamAddDto.getExpireTime() == null ? new Date() : teamAddDto.getExpireTime());
        team.setTCaptainUId(userId);
        team.setTIsPublic(teamAddDto.getIsPublic());
        team.setTPassword(teamAddDto.getPassword());
        int inserted = teamMapper.insert(team);
        if (inserted != 1) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "添加失败");
        }
        //加入关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUId(userId);
        userTeam.setTId(team.getTId());
        userTeam.setJoinTime(new Date());
        if (!userTeamService.userTeamAdd(userTeam)) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "添加用户队伍关系失败");
        }
        return team;
    }

    @Override
    public Team teamDelete(Long id) {
        Team team = teamMapper.selectById(id);
        if (team == null) {
            throw new BusinessException(ErrorStatus.NULL_ERROR, "该队伍不存在");
        }
        int i = teamMapper.deleteById(id);
        if (i != 1) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "删除失败");
        }
        return team;
    }

    @Override
    public Team teamUpdate(TeamUpdateDto teamUpdateDto) {
        NullPointUtil.checkNullPoint(teamUpdateDto);
        Team originTeam = teamMapper.selectById(teamUpdateDto.getTeamId());
        if (originTeam == null) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "队伍不存在");
        }
        //todo:校验修改者是管理员或者队长

        //校验参数是否和原来的一样，一样就不修改
        Team updateTeam = getUpdateTeam(originTeam, teamUpdateDto);
        int i = teamMapper.updateById(updateTeam);
        if (i != 1) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "修改失败");
        }
        return teamMapper.selectById(updateTeam.getTId());
    }

    /**
     * 校验原队伍信息是否和传入进来的队伍信息一致
     * 如果一致就不修改，返回要修改的对象
     *
     * @author CraneResigned
     * @date 2024/10/27 17:50
     **/
    private Team getUpdateTeam(Team originTeam, TeamUpdateDto teamUpdateDto) {
        Team updateTeam = new Team();
        String name = teamUpdateDto.getName();
        String description = teamUpdateDto.getDescription();
        Date expireTime = teamUpdateDto.getExpireTime();
        Integer maxNum = teamUpdateDto.getMaxNum();
        Integer isPublic = teamUpdateDto.getIsPublic();
        String password = teamUpdateDto.getPassword();
        Long captainId = teamUpdateDto.getCaptainId();
        if (!StrUtil.equals(originTeam.getTName(), name)) {
            updateTeam.setTName(name);
        }
        if (!StrUtil.equals(originTeam.getTDescription(), description)) {
            updateTeam.setTDescription(description);
        }
        Date originTeamExpireTime = originTeam.getExpiretime();
        if (expireTime != null && !originTeamExpireTime.equals(expireTime) && expireTime.after(DateUtil.date())) {
            updateTeam.setExpiretime(expireTime);
        }
        Integer originMaxNum = originTeam.getTMaxNum();
        if (!((int) originMaxNum == maxNum) && maxNum > 1) {
            updateTeam.setTMaxNum(maxNum);
        }
        Integer tIsPublic = originTeam.getTIsPublic();
        //如果原来的是否公开和修改的不相等并且修改的这个值要属于这两个枚举
        //todo：这样判断的话增加枚举怎么办？先这样把
        if (!((int) tIsPublic == isPublic)
                && (isPublic.equals(TeamStatusEnum.PUBLIC.getCode())
                || isPublic.equals(TeamStatusEnum.PRIVATE.getCode()))) {
            updateTeam.setTIsPublic(isPublic);
        }
        if (!StrUtil.equals(originTeam.getTPassword(), password)) {
            updateTeam.setTPassword(password);
        }
        User user = userMapper.selectById(captainId);
        if (user == null) {
            throw new BusinessException(ErrorStatus.NULL_ERROR, "找不到该用户");
        }
        if (!captainId.equals(originTeam.getTCaptainUId())) {
            updateTeam.setTCaptainUId(captainId);
        }
        updateTeam.setTId(originTeam.getTId());
        updateTeam.setUpdateTime(new Date());
        return updateTeam;
    }

    @Override
    public List<Team> teamList() {
        return teamMapper.selectList(null);
    }

    @Override
    public Team selectById(Long id) {
        return teamMapper.selectById(id);
    }

    @Override
    public Page<Team> teamPage(TeamQuery teamQuery) {
        NullPointUtil.checkNullPoint(teamQuery);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        return this.page(page);
    }
}




