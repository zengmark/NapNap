package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.entity.Like;
import com.napnap.mapper.LikeMapper;
import com.napnap.service.LikeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 13123
 * @description 针对表【tb_like(点赞表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like>
        implements LikeService {

    @Resource
    private LikeMapper likeMapper;

    /**
     * 一条点赞记录，返回值为 true 代表是点赞，返回值为 false 代表是取消点赞
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public boolean changeLikeStatus(Long userId, Long postId) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getUid, userId);
        queryWrapper.eq(Like::getPostId, postId);
        Like like = likeMapper.selectOne(queryWrapper);
        // 存在记录的情况下，将其逻辑删除
        if (like != null) {
            likeMapper.deleteById(like);
            return false;
        }
        like = new Like();
        like.setUid(userId);
        like.setPostId(postId);
        likeMapper.insert(like);
        return true;
    }

    /**
     * 根据 postId 将所有记录删除
     *
     * @param postId
     * @return
     */
    @Override
    public boolean deleteAllLikeRecord(Long postId) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getPostId, postId);
        likeMapper.delete(queryWrapper);
        return true;
    }
}




