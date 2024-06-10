package com.napnap.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDeleteRequest implements Serializable {

    /**
     * 评论ID
     */
    private Long commentId;

//    /**
//     * 评论类型
//     */
//    private Integer type;

    private static final long serialVersionUID = 1L;
}
