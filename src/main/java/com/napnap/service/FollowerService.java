package com.napnap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.entity.Follower;

import java.util.List;

/**
* @author 13123
* @description 针对表【tb_follower(粉丝表)】的数据库操作Service
* @createDate 2024-06-09 21:56:28
*/
public interface FollowerService extends IService<Follower> {
    boolean addFollower(long followerId);

    boolean deleteFollower(long followerId);

    List<Long> listFollowerIds(long userId, int current, int pageSize);

    List<Long> listFocusIds(long userId, int current, int pageSize);
}
