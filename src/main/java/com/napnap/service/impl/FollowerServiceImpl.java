package com.napnap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.entity.Follower;
import com.napnap.service.FollowerService;
import com.napnap.mapper.FollowerMapper;
import org.springframework.stereotype.Service;

/**
* @author 13123
* @description 针对表【tb_follower(粉丝表)】的数据库操作Service实现
* @createDate 2024-06-09 21:56:28
*/
@Service
public class FollowerServiceImpl extends ServiceImpl<FollowerMapper, Follower>
    implements FollowerService{

}




