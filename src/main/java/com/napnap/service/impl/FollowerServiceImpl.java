package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.entity.Follower;
import com.napnap.entity.User;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.FollowerMapper;
import com.napnap.mapper.UserMapper;
import com.napnap.service.FollowerService;
import com.napnap.service.MessageService;
import com.napnap.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13123
 * @description 针对表【tb_follower(粉丝表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class FollowerServiceImpl extends ServiceImpl<FollowerMapper, Follower>
        implements FollowerService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private FollowerMapper followerMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MessageService messageService;

    /**
     * 添加关注记录
     *
     * @param followerId
     * @return
     */
    @Transactional
    @Override
    public boolean addFollower(long followerId) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        LambdaQueryWrapper<Follower> followerQueryWrapper = new LambdaQueryWrapper<>();
        followerQueryWrapper.eq(Follower::getUid, userId);
        followerQueryWrapper.eq(Follower::getFollowerId, followerId);
        Follower follower = followerMapper.selectOne(followerQueryWrapper);
        if (follower != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该记录已经存在");
        }
        // 添加粉丝记录
        follower = new Follower();
        follower.setUid(userId);
        follower.setFollowerId(followerId);
        followerMapper.insert(follower);
        // 用户粉丝数 + 1
        User user = userMapper.selectById(followerId);
        if(user == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户信息不存在");
        }
        user.setFocusNum(user.getFocusNum() + 1);
        // 发送一条消息到消息表中
        messageService.addMessage(follower.getId(), MessageConstant.FOCUS, followerId);
        return true;
    }

    /**
     * 取消关注记录
     *
     * @param followerId
     * @return
     */
    @Transactional
    @Override
    public boolean deleteFollower(long followerId) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        LambdaQueryWrapper<Follower> followerQueryWrapper = new LambdaQueryWrapper<>();
        followerQueryWrapper.eq(Follower::getUid, userId);
        followerQueryWrapper.eq(Follower::getFollowerId, followerId);
        Follower follower = followerMapper.selectOne(followerQueryWrapper);
        if (follower == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "不存在该粉丝记录");
        }
        // 删除粉丝记录
        followerMapper.deleteById(follower);
        // 用户粉丝数 - 1
        User user = userMapper.selectById(followerId);
        if(user == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户信息不存在");
        }
        user.setFocusNum(user.getFocusNum() - 1);
        // 删除消息表中的消息
        messageService.deleteMessage(follower.getId(), MessageConstant.FOCUS, followerId);
        return true;
    }

    /**
     * 根据用户ID，查出用户的粉丝列表
     *
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public List<Long> listFollowerIds(long userId, int current, int pageSize) {
        LambdaQueryWrapper<Follower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follower::getFollowerId, userId);
//        Page<Follower> followerPage = followerMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
//        List<Follower> followerList = followerPage.getRecords();
        List<Follower> followerList = followerMapper.selectList(queryWrapper);
        return followerList.stream().map(Follower::getUid).collect(Collectors.toList());
//        return new Page<Long>(followerPage.getCurrent(), followerPage.getSize(), followerPage.getTotal()).setRecords(idList);
    }

    /**
     * 根据用户ID，查询用户的关注列表
     *
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public List<Long> listFocusIds(long userId, int current, int pageSize) {
        LambdaQueryWrapper<Follower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follower::getUid, userId);
//        Page<Follower> followerPage = followerMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
//        List<Follower> followerList = followerPage.getRecords();
        List<Follower> followerList = followerMapper.selectList(queryWrapper);
        return followerList.stream().map(Follower::getFollowerId).collect(Collectors.toList());
//        return new Page<Long>(followerPage.getCurrent(), followerPage.getSize(), followerPage.getTotal()).setRecords(idList);
    }


}




