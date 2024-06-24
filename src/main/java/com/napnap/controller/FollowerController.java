package com.napnap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.ResultUtils;
import com.napnap.constant.UserConstant;
import com.napnap.dto.follower.FollowerAddRequest;
import com.napnap.dto.follower.FollowerDeleteRequest;
import com.napnap.entity.Follower;
import com.napnap.exception.BusinessException;
import com.napnap.service.FollowerService;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/follower")
@Api(tags = "关注管理")
public class FollowerController {

    @Resource
    private FollowerService followerService;

    @ApiOperation("测试")
    public String test(){
        return "test";
    }

    @ApiOperation("查看是否关注")
    @LoginCheck
    @PostMapping("/isFollow")
    public BaseResponse<Boolean> isFollow(@RequestBody FollowerAddRequest followerAddRequest, HttpServletRequest request){
        if(followerAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        Long followerId = followerAddRequest.getFollowerId();
        LambdaQueryWrapper<Follower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follower::getUid, userId);
        queryWrapper.eq(Follower::getFollowerId, followerId);
        Follower record = followerService.getOne(queryWrapper);
        return ResultUtils.success(record != null);
    }

    @ApiOperation("用户新增关注")
    @LoginCheck
    @PostMapping("/addFocus")
    public BaseResponse<UserVO> addFocus(@RequestBody FollowerAddRequest followerAddRequest){
        if(followerAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Long followerId = followerAddRequest.getFollowerId();
        UserVO userVO = followerService.addFollower(followerId);
        return ResultUtils.success(userVO);
    }

    @ApiOperation("用户取消关注")
    @LoginCheck
    @PostMapping("/removeFocus")
    public BaseResponse<UserVO> removeFocus(@RequestBody FollowerDeleteRequest followerDeleteRequest){
        if(followerDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Long followerId = followerDeleteRequest.getFollowerId();
        UserVO userVO = followerService.deleteFollower(followerId);
        return ResultUtils.success(userVO);
    }


}
