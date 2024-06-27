package com.napnap.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
public class MessageCommentVO extends UserVO implements Serializable {

    /**
     * 消息ID
     */
    private Long messageId;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String title;

    private static final long serialVersionUID = 1L;
}
