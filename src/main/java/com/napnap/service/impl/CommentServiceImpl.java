package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.constant.CommentConstant;
import com.napnap.constant.UserConstant;
import com.napnap.dto.comment.CommentAddRequest;
import com.napnap.dto.comment.CommentDeleteRequest;
import com.napnap.dto.comment.CommentQueryRequest;
import com.napnap.entity.Comment;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.CommentMapper;
import com.napnap.service.CommentService;
import com.napnap.vo.CommentUnderPostVO;
import com.napnap.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13123
 * @description 针对表【tb_comment(评论表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private CommentMapper commentMapper;

    /**
     * 添加评论
     *
     * @param commentAddRequest
     * @return
     */
    @Override
    public boolean addComment(CommentAddRequest commentAddRequest) {
        Long parentId = commentAddRequest.getParentId();
        Integer type = commentAddRequest.getType();
        String content = commentAddRequest.getContent();
        List<String> picture = commentAddRequest.getPicture();
        // 内容和图片不能同时为空
        if (StringUtils.isEmpty(content) && CollectionUtil.isEmpty(picture)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论和图片不能为空");
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        Comment comment = new Comment();
        comment.setUid(userId);
        comment.setParentId(parentId);
        comment.setCommentType(type);
        comment.setContent(content);
        comment.setPictureList(picture);
        commentMapper.insert(comment);
        return true;
    }

    /**
     * 查出帖子下的所有评论及其子评论
     *
     * @param commentQueryRequest
     * @return
     */
    @Override
    public Page<CommentUnderPostVO> listAllCommentUnderPost(CommentQueryRequest commentQueryRequest) {
        Long postId = commentQueryRequest.getPostId();
        int current = commentQueryRequest.getCurrent();
        int pageSize = commentQueryRequest.getPageSize();
        return getCommentsByPostId(postId, current, pageSize);
    }

    /**
     * 查询帖子下的所有评论
     *
     * @param postId
     * @param current
     * @param pageSize
     * @return
     */
    public Page<CommentUnderPostVO> getCommentsByPostId(Long postId, int current, int pageSize) {
        Page<Comment> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, postId)
                .eq(Comment::getCommentType, CommentConstant.POST)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        List<Comment> commentList = commentPage.getRecords();
        List<CommentUnderPostVO> commentUnderPostVOList = commentList.stream().map(this::getCommentUnderPostVO).collect(Collectors.toList());
        Page<CommentUnderPostVO> commentUnderPostVOPage = new Page<CommentUnderPostVO>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal())
                .setRecords(commentUnderPostVOList);

        for (CommentUnderPostVO commentUnderPostVO : commentUnderPostVOPage.getRecords()) {
            List<CommentUnderPostVO> replies = getCommentsRecursively(commentUnderPostVO.getId(), CommentConstant.COMMENT);
            commentUnderPostVO.setReplies(replies);
        }

        return commentUnderPostVOPage;
    }

    /**
     * 递归查询评论下的子评论
     *
     * @param parentId
     * @param type
     * @return
     */
    private List<CommentUnderPostVO> getCommentsRecursively(Long parentId, Integer type) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, parentId)
                .eq(Comment::getCommentType, type)
                .orderByDesc(Comment::getCreateTime);

        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        List<CommentUnderPostVO> commentUnderPostVOList = commentList.stream().map(this::getCommentUnderPostVO).collect(Collectors.toList());
        for (CommentUnderPostVO commentUnderPostVO : commentUnderPostVOList) {
            List<CommentUnderPostVO> commentsRecursively = getCommentsRecursively(commentUnderPostVO.getId(), CommentConstant.COMMENT);
            commentUnderPostVO.setReplies(commentsRecursively);
        }
        return commentUnderPostVOList;
    }

    /**
     * 删除评论，同时要删除该评论下的所有评论
     *
     * @param commentDeleteRequest
     * @return
     */
    @Override
    public boolean deleteCommentById(CommentDeleteRequest commentDeleteRequest) {
        Long commentId = commentDeleteRequest.getCommentId();
        deleteCommentsRecursively(commentId);
        return true;
    }

    /**
     * 根据帖子 ID 删除这个帖子下的所有评论
     * @param postId
     * @return
     */
    @Override
    public boolean deleteCommentByPostId(Long postId) {
        // 先查出该帖子下的第一级的所有评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, postId);
        queryWrapper.eq(Comment::getCommentType, CommentConstant.POST);
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        // 遍历所有第一级评论，递归删除所有子评论
        for (Comment comment : commentList) {
            deleteCommentsRecursively(comment.getId());
        }
        return true;
    }

    /**
     * 递归删除子评论
     *
     * @param parentId
     */
    private void deleteCommentsRecursively(Long parentId) {
        // 查找所有子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, parentId);
        queryWrapper.eq(Comment::getCommentType, CommentConstant.COMMENT);

        List<Comment> comments = commentMapper.selectList(queryWrapper);
        for (Comment comment : comments) {
            // 递归删除子评论的子评论
            deleteCommentsRecursively(comment.getId());
        }

        // 删除当前评论（逻辑删除）
        commentMapper.deleteById(parentId);
    }

    /**
     * 评论数据脱敏
     * @param comment
     * @return
     */
    private CommentUnderPostVO getCommentUnderPostVO(Comment comment) {
        CommentUnderPostVO commentUnderPostVO = new CommentUnderPostVO();
        BeanUtil.copyProperties(comment, commentUnderPostVO);
        commentUnderPostVO.setPicture(comment.getPictureList());
        return commentUnderPostVO;
    }
}




