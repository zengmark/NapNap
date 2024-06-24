package com.napnap.dto.post;

import com.napnap.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostOtherRequest extends PageRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
