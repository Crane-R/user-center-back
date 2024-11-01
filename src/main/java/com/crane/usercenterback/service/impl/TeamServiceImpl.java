package com.crane.usercenterback.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crane.usercenterback.common.ErrorStatus;
import com.crane.usercenterback.constant.TeamStatusEnum;
import com.crane.usercenterback.exception.BusinessException;
import com.crane.usercenterback.mapper.TeamMapper;
import com.crane.usercenterback.mapper.UserMapper;
import com.crane.usercenterback.mapper.UserTeamMapper;
import com.crane.usercenterback.model.domain.Team;
import com.crane.usercenterback.model.domain.User;
import com.crane.usercenterback.model.domain.UserTeam;
import com.crane.usercenterback.model.domain.vo.TeamVo;
import com.crane.usercenterback.model.domain.vo.UserVo;
import com.crane.usercenterback.model.dto.*;
import com.crane.usercenterback.service.TeamService;
import com.crane.usercenterback.service.UserService;
import com.crane.usercenterback.service.UserTeamService;
import com.crane.usercenterback.utils.NullPointUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    private final UserTeamMapper userTeamMapper;

    /**
     * 事务隔离级别读已提交，避免脏读
     * （即我这里这个方法还没有提交的时候，别的事务是读不到的）
     *
     * @author CraneResigned
     * @date 2024/10/26 12:13
     **/
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TeamVo teamAdd(TeamAddDto teamAddDto, HttpServletRequest request) {
        NullPointUtil.checkNullPoint("添加队伍的参数不能为空", teamAddDto);
        Long userId = userService.userCurrent(request.getSession()).getUserId();
        Team team = new Team();
        team.setTCode(RandomUtil.randomString(6));
        team.setTName(teamAddDto.getName());
        team.setTDescription(teamAddDto.getDescription());
        team.setTMaxNum(teamAddDto.getMaxNum());
        //这里过期时间是要优化处理的，我给个默认过期时间吧
        team.setExpiretime(teamAddDto.getExpireTime() == null ?
                DateUtil.tomorrow() : teamAddDto.getExpireTime());
        team.setTCaptainUId(userId);
        team.setTIsPublic(teamAddDto.getIsPublic());
        team.setTPassword(SecureUtil.md5(teamAddDto.getPassword()));
        int inserted = teamMapper.insert(team);
        if (inserted != 1) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "添加失败");
        }
        //加入关系表
        UserTeamAddDto userTeamAddDto = new UserTeamAddDto();
        userTeamAddDto.setTeamId(team.getTId());
        if (!userTeamService.userTeamAdd(userTeamAddDto, request)) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "添加用户队伍关系失败");
        }
        return team2Vo(teamMapper.selectById(team.getTId()));
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

    @Override
    public Team teamUpdateTimeOnly(Long teamId) {
        TeamUpdateDto originTeamUpdateDto = new TeamUpdateDto();
        originTeamUpdateDto.setTeamId(teamId);
        return teamUpdate(originTeamUpdateDto);
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
    public TeamVo selectById(Long id) {
        return team2Vo(teamMapper.selectById(id));
    }

    @Override
    public Page<TeamVo> teamPage(TeamQuery teamQuery) {
        NullPointUtil.checkNullPoint(teamQuery);
        Page<Team> page = super.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()),
                getTeamQueryWrapper(teamQuery));
        Page<TeamVo> teamVoPage = new Page<>();
        BeanUtil.copyProperties(page, teamVoPage);
        List<TeamVo> teamVoList = new ArrayList<>();
        page.getRecords().forEach(team -> teamVoList.add(team2Vo(team)));
        teamVoPage.setRecords(teamVoList);
        return teamVoPage;
    }

    @Override
    public TeamVo team2Vo(Team team) {
        TeamVo teamVo = new TeamVo();
        teamVo.setTeamId(team.getTId());
        teamVo.setCode(team.getTCode());
        teamVo.setName(team.getTName());
        teamVo.setDescription(team.getTDescription());
        teamVo.setMaxNum(team.getTMaxNum());
        teamVo.setExpireTime(DateUtil.formatDateTime(team.getExpiretime()));
        teamVo.setCaptainId(team.getTCaptainUId());
        teamVo.setIsPublic(team.getTIsPublic());
        teamVo.setCreateTime(team.getCreateTime());
        teamVo.setUpdateTime(team.getUpdateTime());
        //搜索users
        List<UserTeam> userTeams = userTeamMapper.selectList(new QueryWrapper<UserTeam>().eq("t_id", team.getTId()));
        if (CollectionUtil.isNotEmpty(userTeams)) {
            List<UserVo> userVos = userMapper.selectList(new QueryWrapper<User>().in("user_id", userTeams)).stream()
                    .map(userService::user2Vo).collect(Collectors.toList());
            teamVo.setUserList(userVos);
        }
        return teamVo;
    }

    @Override
    public Team vo2Team(TeamVo teamVo) {
        Team team = new Team();
        team.setTId(teamVo.getTeamId());
        team.setTCode(teamVo.getCode());
        team.setTName(teamVo.getName());
        team.setTDescription(teamVo.getDescription());
        team.setTMaxNum(teamVo.getMaxNum());
        team.setExpiretime(DateUtil.parse(teamVo.getExpireTime()));
        team.setTCaptainUId(teamVo.getCaptainId());
        team.setTIsPublic(teamVo.getIsPublic());
        team.setCreateTime(teamVo.getCreateTime());
        team.setUpdateTime(teamVo.getUpdateTime());
        return team;
    }

    /**
     * 获取查询的QueryWrapper封装体
     *
     * @author CraneResigned
     * @date 2024/10/27 18:20
     **/
    private QueryWrapper<Team> getTeamQueryWrapper(TeamQuery teamQuery) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        String code = teamQuery.getCode();
        String name = teamQuery.getName();
        String description = teamQuery.getDescription();
        Integer maxNum = teamQuery.getMaxNum();
        Long captainId = teamQuery.getCaptainId();
        if (StrUtil.isNotBlank(code)) {
            queryWrapper.eq("t_code", code);
        }
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("t_name", name);
        }
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.like("t_description", description);
        }
        if (maxNum != null && maxNum > 1) {
            queryWrapper.eq("t_max_num", maxNum);
        }
        if (captainId != null) {
            queryWrapper.eq("t_captain_u_id", captainId);
        }
        //只查询未过期的队伍
        queryWrapper.ge("expireTime", new Date());
        //只查询公开和加密的队伍
        queryWrapper.eq("t_is_public", TeamStatusEnum.PUBLIC.getCode())
                .or().eq("t_is_public", TeamStatusEnum.ENCRYPT.getCode());

        return queryWrapper;
    }

    @Override
    public Boolean teamDisband(Long teamId, HttpServletRequest request) {
        UserVo userCurrent = userService.userCurrent(request.getSession());
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "队伍不存在");
        }
        if (!NumberUtil.equals(userCurrent.getUserId(), team.getTCaptainUId())) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "你无权解散该队伍");
        }
        int i = teamMapper.deleteById(teamId);
        if (i != 1) {
            throw new BusinessException(ErrorStatus.SYSTEM_ERROR, "删除失败");
        }
        userTeamMapper.delete(new QueryWrapper<UserTeam>().eq("t_id", teamId));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamVo teamQuit(Long teamId, HttpServletRequest request) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorStatus.PARAM_ERROR, "队伍不存在");
        }
        Long userId = userService.userCurrent(request.getSession()).getUserId();
        QueryWrapper<UserTeam> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("u_id", userId);
        deleteWrapper.eq("t_id", teamId);
        int delete = userTeamMapper.delete(deleteWrapper);
        if (delete != 1) {
            throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "删除失败");
        }
        //如果是队长就顺延
        if (!NumberUtil.equals(userId, team.getTCaptainUId())) {
            return team2Vo(team);
        }
        QueryWrapper<UserTeam> selectCaptainWrapper = new QueryWrapper<>();
        selectCaptainWrapper.eq("t_id", teamId);
        selectCaptainWrapper.orderByDesc("join_time");
        UserTeam userTeam = userTeamMapper.selectOne(selectCaptainWrapper);
        //队伍已经没人，调用解散
        if (userTeam == null) {
            if (!teamDisband(teamId, request)) {
                throw new BusinessException(ErrorStatus.BUSINESS_ERROR, "解散失败");
            }
            return team2Vo(team);
        }
        TeamUpdateDto teamUpdateDto = new TeamUpdateDto();
        teamUpdateDto.setTeamId(teamId);
        teamUpdateDto.setCaptainId(userTeam.getUId());
        Team teamUpdate = teamUpdate(teamUpdateDto);
        return team2Vo(teamUpdate);
    }

    @Override
    public List<TeamVo> teamListUserJoin(HttpServletRequest request) {
        Long userId = userService.userCurrent(request.getSession()).getUserId();
        List<Long> teamIds = userTeamMapper.selectList(new QueryWrapper<UserTeam>()
                .eq("u_id", userId)).stream().map(UserTeam::getTId).collect(Collectors.toList());
        return teamMapper.selectList(new QueryWrapper<Team>().in("t_id", teamIds))
                .stream().map(this::team2Vo).collect(Collectors.toList());
    }
}




