package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.common.PageRequest;
import com.napnap.dto.user.UserLoginRequest;
import com.napnap.dto.user.UserRegisterRequest;
import com.napnap.dto.user.UserUpdateRequest;
import com.napnap.entity.User;
import com.napnap.vo.UserVO;

/**
* @author 13123
* @description 针对表【tb_user(用户表)】的数据库操作Service
* @createDate 2024-06-08 15:42:06
*/
public interface UserService extends IService<User> {

    UserVO register(UserRegisterRequest userRegisterRequest);

    UserVO login(UserLoginRequest userLoginRequest);

    UserVO updateUserInfo(UserUpdateRequest userUpdateRequest);

    void changeUserFans(long userId, long num);

    void changeUserFocus(long userId, long num);

    Page<UserVO> listUserFocus(PageRequest pageRequest);

    Page<UserVO> listUserFollowers(PageRequest pageRequest);

    UserVO getUserVO(User user);
}
