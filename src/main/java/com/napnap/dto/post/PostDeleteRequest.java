package com.napnap.dto.post;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostDeleteRequest implements Serializable {

    /**
     * 删除帖子ID
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
