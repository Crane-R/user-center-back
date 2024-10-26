package com.crane.usercenterback.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.TeamMapper;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.UserTeam;
import com.crane.usercenterback.model.dto.PageDto;
import com.crane.usercenterback.model.dto.TeamAddDto;
import com.crane.usercenterback.model.dto.TeamQuery;
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
    public Team teamUpdate(Team team) {
        int i = teamMapper.updateById(team);
        if (i != 1) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "修改失败");
        }
        return teamMapper.selectById(team.getTId());
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




