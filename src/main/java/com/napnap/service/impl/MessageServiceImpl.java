package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.entity.Message;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.MessageMapper;
import com.napnap.service.MessageService;
import com.napnap.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author 13123
 * @description 针对表【tb_message(消息表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private MessageMapper messageMapper;

    /**
     * 添加消息
     *
     * @param sourceId
     * @param type
     * @return
     */
    @Override
    public boolean addMessage(long sourceId, int type) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getSourceId, sourceId);
        queryWrapper.eq(Message::getMessageType, type);
        Message message = messageMapper.selectOne(queryWrapper);
        if (message != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已存在相同消息，无法重复添加");
        }
        message = new Message();
        message.setSourceId(sourceId);
        message.setMessageType(type);
        messageMapper.insert(message);
        return true;
    }

    /**
     * 删除消息
     *
     * @param sourceId
     * @param type
     * @return
     */
    @Override
    public boolean deleteMessage(long sourceId, int type) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getSourceId, sourceId);
        queryWrapper.eq(Message::getMessageType, type);
        Message message = messageMapper.selectOne(queryWrapper);
        if (message == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除的消息不存在");
        }
        messageMapper.deleteById(message);
        return true;
    }

    /**
     * 获取用户的消息列表各个种类的数量
     *
     * @return
     */
    @Override
    public List<Long> listMessageCount() {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 查询消息列表，分别要查四种的数量，包括：0 新增关注，1 新增点赞，2 新增收藏，3 新增回复
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        // 1、查询关注的数量
        queryWrapper.eq(Message::getMessageType, MessageConstant.FOCUS);
        queryWrapper.eq(Message::getUid, userId);
        queryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        Long focusNum = messageMapper.selectCount(queryWrapper);
        // 2、查询点赞的数量
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getMessageType, MessageConstant.LIKE);
        queryWrapper.eq(Message::getUid, userId);
        queryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        Long likeNum = messageMapper.selectCount(queryWrapper);
        // 3、查询收藏的数量
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getMessageType, MessageConstant.COLLECT);
        queryWrapper.eq(Message::getUid, userId);
        queryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        Long collectNum = messageMapper.selectCount(queryWrapper);
        // 4、查询评论的数量
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getMessageType, MessageConstant.COMMENT);
        queryWrapper.eq(Message::getUid, userId);
        queryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        Long commentNum = messageMapper.selectCount(queryWrapper);
        // 汇总数量
        List<Long> idList = Arrays.asList(focusNum, likeNum, collectNum, commentNum);
        return idList;
    }
}




