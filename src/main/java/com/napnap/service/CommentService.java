package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.dto.comment.CommentAddRequest;
import com.napnap.dto.comment.CommentDeleteRequest;
import com.napnap.dto.comment.CommentQueryRequest;
import com.napnap.entity.Comment;
import com.napnap.vo.CommentUnderPostVO;

/**
* @author 13123
* @description 针对表【tb_comment(评论表)】的数据库操作Service
* @createDate 2024-06-09 21:56:28
*/
public interface CommentService extends IService<Comment> {

    boolean addComment(CommentAddRequest commentAddRequest);

    Page<CommentUnderPostVO> listAllCommentUnderPost(CommentQueryRequest commentQueryRequest);

    boolean deleteCommentById(CommentDeleteRequest commentDeleteRequest);

    boolean deleteCommentByPostId(Long postId);
}
