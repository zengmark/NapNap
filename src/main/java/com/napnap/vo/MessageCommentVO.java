package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageCommentVO extends UserVO implements Serializable {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论的帖子或评论的内容的ID
     */
    private Long commentParentId;

    /**
     * 被评论的帖子的标题或者被评论的评论的内容
     */
    private String commentParentContent;

    private static final long serialVersionUID = 1L;
}
