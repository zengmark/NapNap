package com.napnap.dto.follower;

import lombok.Data;

import java.io.Serializable;

@Data
public class FollowerDeleteRequest implements Serializable {

    /**
     * 被关注用户ID
     */
    private Long followerId;

    private static final long serialVersionUID = 1L;
}
