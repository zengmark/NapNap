package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CommentVO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 评论用户ID
     */
    private Long uid;

    /**
     * 评论的帖子或评论的评论的ID
     */
    private Long parentId;

    /**
     * 评论类型，0 是评论帖子，1 是评论评论
     */
    private Integer commentType;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 图片地址
     */
    private List<String> picture;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
