package com.napnap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.entity.Like;

/**
* @author 13123
* @description 针对表【tb_like(点赞表)】的数据库操作Service
* @createDate 2024-06-09 21:56:28
*/
public interface LikeService extends IService<Like> {
    boolean changeLikeStatus(Long userId, Long postId);

    boolean deleteAllLikeRecord(Long postId);
}
