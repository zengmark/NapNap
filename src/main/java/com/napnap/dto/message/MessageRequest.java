package com.napnap.dto.message;

import com.napnap.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageRequest extends PageRequest implements Serializable {

    /**
     * 标识拉取的是可读消息还是全部消息
     */
    private Integer isVisible;

    private static final long serialVersionUID = 1L;
}
