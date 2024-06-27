package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.entity.Like;
import com.napnap.entity.Post;
import com.napnap.mapper.LikeMapper;
import com.napnap.mapper.PostMapper;
import com.napnap.service.LikeService;
import com.napnap.service.MessageService;
import com.napnap.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 13123
 * @description 针对表【tb_like(点赞表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like>
        implements LikeService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private PostMapper postMapper;

    /**
     * 一条点赞记录，返回值为 true 代表是点赞，返回值为 false 代表是取消点赞
     *
     * @param userId
     * @param postId
     * @return
     */
    @Transactional
    @Override
    public boolean changeLikeStatus(Long userId, Long postId) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getUid, userId);
        queryWrapper.eq(Like::getPostId, postId);
        Like like = likeMapper.selectOne(queryWrapper);
        // 存在记录的情况下，将其逻辑删除
        if (like != null) {
            likeMapper.deleteById(like);
            Post post = postMapper.selectById(postId);
            userId = post.getUserId();
            messageService.deleteMessage(like.getId(), MessageConstant.LIKE, userId);
            return false;
        }
        like = new Like();
        like.setUid(userId);
        like.setPostId(postId);
        likeMapper.insert(like);
        Post post = postMapper.selectById(postId);
        userId = post.getUserId();
        messageService.addMessage(like.getId(), MessageConstant.LIKE, userId);
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
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getPostId, postId);
        List<Like> likeList = likeMapper.selectList(queryWrapper);
        for (Like like : likeList) {
            messageService.deleteMessage(like.getId(), MessageConstant.LIKE, userId);
        }
        likeMapper.delete(queryWrapper);
        return true;
    }
}




