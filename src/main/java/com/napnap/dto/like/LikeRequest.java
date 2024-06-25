package com.napnap.dto.like;

import lombok.Data;

import java.io.Serializable;

@Data
public class LikeRequest implements Serializable {

    /**
     * 帖子ID
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
