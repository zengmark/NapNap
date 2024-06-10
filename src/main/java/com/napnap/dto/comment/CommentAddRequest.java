package com.napnap.dto.comment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CommentAddRequest implements Serializable {

    /**
     * 被评论的帖子或被评论的评论ID
     */
    private Long parentId;

    /**
     * 评论的类型，0 评论帖子，1 评论评论
     */
    private Integer type;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 评论所携带的图片地址
     */
    private List<String> picture;

    private static final long serialVersionUID = 1L;
}
