package com.napnap.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MessageReadRequest implements Serializable {

    /**
     * 消息ID
     */
    private List<Long> messageIdList;

    private static final long serialVersionUID = 1L;
}
