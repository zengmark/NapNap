package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.constant.UserConstant;
import com.napnap.dto.user.UserLoginRequest;
import com.napnap.dto.user.UserRegisterRequest;
import com.napnap.dto.user.UserUpdateRequest;
import com.napnap.entity.User;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.UserMapper;
import com.napnap.service.UserService;
import com.napnap.utils.PasswordUtil;
import com.napnap.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 13123
 * @description 针对表【tb_user(用户表)】的数据库操作Service实现
 * @createDate 2024-06-08 15:42:06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @Override
    public UserVO register(UserRegisterRequest userRegisterRequest) {
        // 判断用户是否存在，如果存在则抛出异常
        String userName = userRegisterRequest.getUserName();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        if (userAccount.length() > 16 || userAccount.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度必须在 6 - 16 个字符之间");
        }
        if (userPassword.length() > 16 || userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度必须在 6 - 16 个字符之间");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(userAccount), User::getUserAccount, userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户已存在");
        }
        // 用户不存在，向数据库中插入数据，同时要进行 Base64 编码存储密码
        String encryptedPassword = PasswordUtil.encryptPassword(userPassword);
        user = new User();
        user.setUserName(userName);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        userMapper.insert(user);
        return getUserVO(user);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @Override
    public UserVO login(UserLoginRequest userLoginRequest) {
        // 判断用户是否存在，不存在抛出异常
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String encryptedPassword = PasswordUtil.encryptPassword(userPassword);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(userAccount), User::getUserAccount, userAccount);
        queryWrapper.eq(StringUtils.isNotEmpty(encryptedPassword), User::getUserPassword, encryptedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 如果用户已存在
        UserVO userVO = getUserVO(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @return
     */
    @Override
    public UserVO updateUserInfo(UserUpdateRequest userUpdateRequest) {
        // 从数据库中将用户信息查出来，然后更新信息
        Long id = userUpdateRequest.getId();
        String userName = userUpdateRequest.getUserName();
        String userAvatar = userUpdateRequest.getUserAvatar();
        String userProfile = userUpdateRequest.getUserProfile();
        String userPassword = userUpdateRequest.getUserPassword();
        String encryptedPassword = PasswordUtil.encryptPassword(userPassword);
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        user.setUserName(userName);
        user.setUserAvatar(userAvatar);
        user.setUserProfile(userProfile);
        user.setUserPassword(encryptedPassword);
        userMapper.updateById(user);
        return getUserVO(user);
    }

    /**
     * 用户粉丝数量 + num（num 可能为整数，可能为复数）
     *
     * @param userId
     * @param num
     */
    @Override
    public void changeUserFans(long userId, long num) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        user.setFansNum(user.getFansNum() + num);
        userMapper.updateById(user);
    }

    /**
     * 用户关注数量 + num（num 可能为整数，可能为负数）
     *
     * @param userId
     * @param num
     */
    @Override
    public void changeUserFocus(long userId, long num) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        user.setFocusNum(user.getFocusNum() + num);
        userMapper.updateById(user);
    }

    /**
     * 用户数据脱敏
     *
     * @param user
     * @return
     */
    private UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
}




