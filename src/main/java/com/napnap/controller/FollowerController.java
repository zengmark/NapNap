package com.napnap.controller;

import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.ResultUtils;
import com.napnap.dto.follower.FollowerAddRequest;
import com.napnap.dto.follower.FollowerDeleteRequest;
import com.napnap.exception.BusinessException;
import com.napnap.service.FollowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @ApiOperation("用户新增关注")
    @LoginCheck
    @PostMapping("/addFocus")
    public BaseResponse<Boolean> addFocus(@RequestBody FollowerAddRequest followerAddRequest){
        if(followerAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Long followerId = followerAddRequest.getFollowerId();
        boolean flag = followerService.addFollower(followerId);
        return ResultUtils.success(flag);
    }

    @ApiOperation("用户取消关注")
    @LoginCheck
    @PostMapping("/removeFocus")
    public BaseResponse<Boolean> removeFocus(@RequestBody FollowerDeleteRequest followerDeleteRequest){
        if(followerDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Long followerId = followerDeleteRequest.getFollowerId();
        boolean flag = followerService.deleteFollower(followerId);
        return ResultUtils.success(flag);
    }


}
