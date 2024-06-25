package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.constant.UserConstant;
import com.napnap.dto.user.UserLoginRequest;
import com.napnap.dto.user.UserRegisterRequest;
import com.napnap.dto.user.UserSearchRequest;
import com.napnap.dto.user.UserUpdateRequest;
import com.napnap.entity.User;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.UserMapper;
import com.napnap.service.FollowerService;
import com.napnap.service.UserService;
import com.napnap.utils.PasswordUtil;
import com.napnap.vo.UserVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    @Resource
    private FollowerService followerService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @Override
    public String register(UserRegisterRequest userRegisterRequest) {
        // 判断用户是否存在，如果存在则抛出异常
        String userName = userRegisterRequest.getUserName();
//        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
//        if (userAccount.length() > 16 || userAccount.length() < 6) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度必须在 6 - 16 个字符之间");
//        }
//        if (userPassword.length() > 16 || userPassword.length() < 6) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度必须在 6 - 16 个字符之间");
//        }
        // 生成账号
        String userAccount = null;
        Random random = new Random();
        while(true){
            int accountNumber = 100000 + random.nextInt(900000);
            userAccount = String.valueOf(accountNumber);
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            User user = userMapper.selectOne(queryWrapper);
            if(user == null){
                break;
            }
        }
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(StringUtils.isNotEmpty(userAccount), User::getUserAccount, userAccount);
//        User user = userMapper.selectOne(queryWrapper);
//        if (user != null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户已存在");
//        }
        // 用户不存在，向数据库中插入数据，同时要进行 Base64 编码存储密码
        String encryptedPassword = PasswordUtil.encryptPassword(userPassword);
        User user = new User();
        user.setUserName(userName);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        userMapper.insert(user);
        return userAccount;
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
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long id = userVO.getId();
        String userName = userUpdateRequest.getUserName();
        String userAvatar = userUpdateRequest.getUserAvatar();
        String userProfile = userUpdateRequest.getUserProfile();
//        String userPassword = userUpdateRequest.getUserPassword();
//        String encryptedPassword = PasswordUtil.encryptPassword(userPassword);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        user.setUserName(userName);
        user.setUserAvatar(userAvatar);
        user.setUserProfile(userProfile);
//        user.setUserPassword(encryptedPassword);
        userMapper.updateById(user);
        userVO = getUserVO(user);
//        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userVO);
        return userVO;
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
     * 获取用户关注列表
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<UserVO> listUserFocus(PageRequest pageRequest) {
        int current = pageRequest.getCurrent();
        int pageSize = pageRequest.getPageSize();
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        List<Long> idList = followerService.listFocusIds(userId, current, pageSize);
        if(idList.isEmpty()){
            return new Page<>();
        }
        // 根据 IdList 查询所有关注用户信息返回
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(idList)) {
            queryWrapper.in(User::getId, idList);
        }
        Page<User> userPage = userMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        List<User> userList = userPage.getRecords();
        List<UserVO> userVoList = userList.stream().map(this::getUserVO).collect(Collectors.toList());
        return new Page<UserVO>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal()).setRecords(userVoList);
    }

    /**
     * 获取用户粉丝列表
     *
     * @param pageRequest
     * @return
     */
    public Page<UserVO> listUserFollowers(PageRequest pageRequest) {
        int current = pageRequest.getCurrent();
        int pageSize = pageRequest.getPageSize();
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        List<Long> idList = followerService.listFollowerIds(userId, current, pageSize);
        // 根据 idList 查询所有粉丝用户信息返回
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(idList)) {
            queryWrapper.in(User::getId, idList);
        }
        Page<User> userPage = userMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        List<User> userList = userPage.getRecords();
        List<UserVO> userVoList = userList.stream().map(this::getUserVO).collect(Collectors.toList());
        return new Page<UserVO>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal()).setRecords(userVoList);
    }

    /**
     * 用户数据脱敏
     *
     * @param user
     * @return
     */
    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 根据搜索条件搜索用户
     *
     * @param userSearchRequest
     * @return
     */
    @Override
    public Page<UserVO> listAllUserBySearch(UserSearchRequest userSearchRequest) {
        String searchText = userSearchRequest.getSearchText();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(searchText)){
            queryWrapper.and(wrapper -> wrapper.like(User::getUserName, searchText).or().like(User::getUserAccount, searchText));
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(new Page<>(userSearchRequest.getCurrent(), userSearchRequest.getPageSize()), queryWrapper);
        List<User> userList = userPage.getRecords();
        List<UserVO> userVoList = userList.stream().map(this::getUserVO).collect(Collectors.toList());
        return new Page<UserVO>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal()).setRecords(userVoList);
    }
}




