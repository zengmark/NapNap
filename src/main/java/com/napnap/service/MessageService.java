package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.common.PageRequest;
import com.napnap.dto.message.MessageRequest;
import com.napnap.entity.Message;
import com.napnap.vo.*;

import java.util.List;

/**
* @author 13123
* @description 针对表【tb_message(消息表)】的数据库操作Service
* @createDate 2024-06-09 21:56:28
*/
public interface MessageService extends IService<Message> {
    boolean addMessage(long sourceId, int type, long userId);

    boolean deleteMessage(long sourceId, int type, long userId);

    List<Long> listMessageCount();

    Page<MessageUserVO> listMessageByFocus(MessageRequest messageRequest);

    Page<MessageLikeVO> listMessageByLike(MessageRequest messageRequest);

    Page<MessageCollectVO> listMessageByCollect(MessageRequest messageRequest);

    Page<MessageCommentVO> listMessageByComment(MessageRequest messageRequest);

    void setMessageInVisible(List<Long> sourceIdList);
}
