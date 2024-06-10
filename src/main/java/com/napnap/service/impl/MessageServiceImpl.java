package com.napnap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.entity.Message;
import com.napnap.mapper.MessageMapper;
import com.napnap.service.MessageService;
import org.springframework.stereotype.Service;

/**
* @author 13123
* @description 针对表【tb_message(消息表)】的数据库操作Service实现
* @createDate 2024-06-09 21:56:28
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




