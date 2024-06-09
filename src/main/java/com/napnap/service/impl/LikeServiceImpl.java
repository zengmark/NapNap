package com.napnap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.entity.Like;
import com.napnap.service.LikeService;
import com.napnap.mapper.LikeMapper;
import org.springframework.stereotype.Service;

/**
* @author 13123
* @description 针对表【tb_like(点赞表)】的数据库操作Service实现
* @createDate 2024-06-09 21:56:28
*/
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like>
    implements LikeService{

}




