package com.napnap.dto.comment;

import com.napnap.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子ID
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
