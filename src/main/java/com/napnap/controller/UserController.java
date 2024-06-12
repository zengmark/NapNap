package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.common.ResultUtils;
import com.napnap.constant.UserConstant;
import com.napnap.dto.user.UserLoginRequest;
import com.napnap.dto.user.UserRegisterRequest;
import com.napnap.dto.user.UserUpdateRequest;
import com.napnap.exception.BusinessException;
import com.napnap.service.UserService;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @ApiOperation(value = "测试接口", notes = "用于测试接口是否能够正常访问")
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @ApiOperation(value = "用户注册", notes = "注册一个新用户")
    @PostMapping("/register")
    public BaseResponse<UserVO> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入参数为空");
        }
        String userName = userRegisterRequest.getUserName();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        if (StringUtils.isAnyEmpty(userName, userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名/用户账号/用户密码不能为空");
        }
        UserVO userVO = userService.register(userRegisterRequest);
        return ResultUtils.success(userVO);
    }

    @ApiOperation(value = "用户登录", notes = "用户使用账号和密码登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserLoginRequest userLoginRequest){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyEmpty(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号/用户密码不能为空");
        }
        UserVO userVO = userService.login(userLoginRequest);
        return ResultUtils.success(userVO);
    }

    @ApiOperation("退出登录")
    @LoginCheck
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(){
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return ResultUtils.success(true);
    }

    @ApiOperation(value = "获取登录用户信息", notes = "获取登录用户信息")
    @GetMapping("/getLoginUser")
    public BaseResponse<UserVO> getLoginUser(){
        Object loginUser = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户还未登录");
        }
        UserVO userVO = (UserVO) loginUser;
        return ResultUtils.success(userVO);
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @LoginCheck
    @PutMapping("/updateUserInfo")
    public BaseResponse<UserVO> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest){
        if(userUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的参数为空");
        }
        UserVO userVO = userService.updateUserInfo(userUpdateRequest);
        return ResultUtils.success(userVO);
    }

    @ApiOperation("获取用户关注列表")
    @LoginCheck
    @PostMapping("/listUserFocus")
    public BaseResponse<Page<UserVO>> listUserFocus(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<UserVO> userVOPage = userService.listUserFocus(pageRequest);
        return ResultUtils.success(userVOPage);
    }

    @ApiOperation("获取用户粉丝列表")
    @LoginCheck
    @PostMapping("/listUserFollowers")
    public BaseResponse<Page<UserVO>> listUserFollowers(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<UserVO> userVOPage = userService.listUserFollowers(pageRequest);
        return ResultUtils.success(userVOPage);
    }

//    /**
//     * 用户数据脱敏
//     *
//     * @param user
//     * @return
//     */
//    private UserVO getUserVO(User user) {
//        UserVO userVO = new UserVO();
//        BeanUtil.copyProperties(user, userVO);
//        return userVO;
//    }
}
