package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.common.PageRequest;
import com.napnap.entity.Message;
import com.napnap.vo.MessageCollectVO;
import com.napnap.vo.MessageCommentVO;
import com.napnap.vo.MessageLikeVO;
import com.napnap.vo.UserVO;

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

    Page<UserVO> listMessageByFocus(PageRequest pageRequest);

    Page<MessageLikeVO> listMessageByLike(PageRequest pageRequest);

    Page<MessageCollectVO> listMessageByCollect(PageRequest pageRequest);

    Page<MessageCommentVO> listMessageByComment(PageRequest pageRequest);
}
