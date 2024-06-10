package com.napnap.dto.collect;

import lombok.Data;

import java.io.Serializable;

@Data
public class CollectRequest implements Serializable {

    /**
     * 被收藏的帖子/游戏ID
     */
    private Long collectId;

    /**
     * 收藏类型：0 帖子，1 游戏
     */
    private Integer type;

    private static final long serialVersionUID = 1L;
}
