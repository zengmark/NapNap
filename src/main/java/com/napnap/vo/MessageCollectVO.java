package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageCollectVO extends UserVO implements Serializable {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String postTitle;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
