package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageCollectVO extends UserVO implements Serializable {

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String postTitle;

    private static final long serialVersionUID = 1L;
}
