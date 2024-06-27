package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.constant.CollectConstant;
import com.napnap.constant.CommentConstant;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.dto.message.MessageRequest;
import com.napnap.entity.*;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.MessageMapper;
import com.napnap.service.*;
import com.napnap.vo.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private PostService postService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    @Resource
    private FollowerService followerService;

//    @Resource
//    private UserMapper userMapper;

    /**
     * 添加消息
     *
     * @param sourceId
     * @param type
     * @param userId
     * @return
     */
    @Override
    public boolean addMessage(long sourceId, int type, long userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getSourceId, sourceId);
        queryWrapper.eq(Message::getMessageType, type);
        queryWrapper.eq(Message::getUid, userId);
        Message message = messageMapper.selectOne(queryWrapper);
        if (message != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已存在相同消息，无法重复添加");
        }
        message = new Message();
        message.setUid(userId);
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
     * @param userId
     * @return
     */
    @Override
    public boolean deleteMessage(long sourceId, int type, long userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getSourceId, sourceId);
        queryWrapper.eq(Message::getMessageType, type);
        queryWrapper.eq(Message::getUid, userId);
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
        return Arrays.asList(focusNum, likeNum, collectNum, commentNum);
    }

    /**
     * 获取关注列表的未读消息
     *
     * @param messageRequest
     * @return
     */
    @Override
    public Page<MessageUserVO> listMessageByFocus(MessageRequest messageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是粉丝表的主键ID
//        List<Long> sourceIdList = getSourceIdList(userId, MessageConstant.FOCUS);
        List<Message> messageList = getSourceIdList(userId, MessageConstant.FOCUS, messageRequest.getIsVisible());
        if (messageList.isEmpty()) {
            return new Page<>();
        }
        List<Long> sourceIdList = messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
        Map<Long, Long> messageMap = messageList.stream().collect(Collectors.toMap(Message::getSourceId, Message::getId));
        // 将消息设置为 INVISIBLE
//        setMessageInVisible(sourceIdList);
        // 查询用户ID
        LambdaQueryWrapper<Follower> followerQueryWrapper = new LambdaQueryWrapper<>();
        followerQueryWrapper.in(Follower::getId, sourceIdList);
        followerQueryWrapper.orderByDesc(Follower::getCreateTime);
        Page<Follower> followerPage = followerService.page(new Page<>(messageRequest.getCurrent(), messageRequest.getPageSize()), followerQueryWrapper);
        List<Follower> followerList = followerPage.getRecords();
        List<MessageUserVO> userVOList = followerList.stream().map(follower -> {
            MessageUserVO messageUserVO = new MessageUserVO();
            Long uId = follower.getUid();
            // 获取到关注者，将其封装为一个UserVO对象
            User user = userService.getById(uId);
            UserVO followerUserVO = userService.getUserVO(user);
            BeanUtil.copyProperties(followerUserVO, messageUserVO);
            messageUserVO.setMessageId(messageMap.get(follower.getId()));
            messageUserVO.setCreateTime(follower.getCreateTime());
            return messageUserVO;
        }).collect(Collectors.toList());
        return new Page<MessageUserVO>(followerPage.getCurrent(), followerPage.getSize(), followerPage.getTotal()).setRecords(userVOList);
    }

    /**
     * 获取点赞列表的未读消息
     *
     * @param messageRequest
     * @return
     */
    @Override
    public Page<MessageLikeVO> listMessageByLike(MessageRequest messageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是点赞表的主键ID
        List<Message> messageList = getSourceIdList(userId, MessageConstant.LIKE, messageRequest.getIsVisible());
        if (messageList.isEmpty()) {
            return new Page<>();
        }
        List<Long> sourceIdList = messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
        Map<Long, Long> messageMap = messageList.stream().collect(Collectors.toMap(Message::getSourceId, Message::getId));
        // 将消息设置为 INVISIBLE
//        setMessageInVisible(sourceIdList);
        // 获取点赞列表的ID
        LambdaQueryWrapper<Like> likeQueryWrapper = new LambdaQueryWrapper<>();
        likeQueryWrapper.in(Like::getId, sourceIdList);
        Page<Like> likePage = likeService.page(new Page<>(messageRequest.getCurrent(), messageRequest.getPageSize()), likeQueryWrapper);
        List<Like> likeList = likePage.getRecords();
//        List<Like> likeList = likeService.list(likeQueryWrapper);
        // 根据点赞表的信息，拿到对应的帖子信息并封装成 MessageLikeVO
        List<MessageLikeVO> messageLikeVOList = likeList.stream().map(like -> {
            MessageLikeVO messageLikeVO = new MessageLikeVO();
            Long uId = like.getUid();
            Long postId = like.getPostId();
            // 根据 uId 获取点赞人的User信息
            User user = userService.getById(uId);
            UserVO userVOInfo = userService.getUserVO(user);
            // 根据 postId 获取帖子的信息
            Post post = postService.getById(postId);
            BeanUtil.copyProperties(userVOInfo, messageLikeVO);
            messageLikeVO.setPostId(postId);
            messageLikeVO.setPostTitle(post.getTitle());
            messageLikeVO.setMessageId(messageMap.get(like.getId()));
            messageLikeVO.setCreateTime(like.getCreateTime());
            return messageLikeVO;
        }).collect(Collectors.toList());
        return new Page<MessageLikeVO>(likePage.getCurrent(), likePage.getSize(), likePage.getTotal()).setRecords(messageLikeVOList);
    }

    /**
     * 获取收藏列表的未读消息
     *
     * @param messageRequest
     * @return
     */
    @Override
    public Page<MessageCollectVO> listMessageByCollect(MessageRequest messageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是收藏表的主键ID
        List<Message> messageList = getSourceIdList(userId, MessageConstant.COLLECT, messageRequest.getIsVisible());
        if (messageList.isEmpty()) {
            return new Page<>();
        }
        List<Long> sourceIdList = messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
        Map<Long, Long> messageMap = messageList.stream().collect(Collectors.toMap(Message::getSourceId, Message::getId));
        // 将消息设置为 INVISIBLE
//        setMessageInVisible(sourceIdList);
        // 查询用户ID
        LambdaQueryWrapper<Collect> collectQueryWrapper = new LambdaQueryWrapper<>();
        collectQueryWrapper.in(Collect::getId, sourceIdList);
        collectQueryWrapper.eq(Collect::getCollectType, CollectConstant.POST);
        Page<Collect> collectPage = collectService.page(new Page<>(messageRequest.getCurrent(), messageRequest.getPageSize()), collectQueryWrapper);
        List<Collect> collectList = collectPage.getRecords();
        List<MessageCollectVO> messageLikeVOList = collectList.stream().map(collect -> {
            MessageCollectVO messageCollectVO = new MessageCollectVO();
            // 根据 uId 获取收藏用户信息
            Long uId = collect.getUid();
            User user = userService.getById(uId);
            UserVO userVoInfo = userService.getUserVO(user);
            // 根据 collectedId 获取帖子信息
            Long collectedId = collect.getCollectedId();
            Post post = postService.getById(collectedId);
            BeanUtil.copyProperties(userVoInfo, messageCollectVO);
            messageCollectVO.setPostId(collectedId);
            messageCollectVO.setPostTitle(post.getTitle());
            messageCollectVO.setMessageId(messageMap.get(collect.getId()));
            messageCollectVO.setCreateTime(collect.getCreateTime());
            return messageCollectVO;
        }).collect(Collectors.toList());
        return new Page<MessageCollectVO>(collectPage.getCurrent(), collectPage.getSize(), collectPage.getTotal()).setRecords(messageLikeVOList);
    }

    /**
     * 获取评论列表的未读消息
     *
     * @param messageRequest
     * @return
     */
    @Override
    public Page<MessageCommentVO> listMessageByComment(MessageRequest messageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是评论表的主键ID
        List<Message> messageList = getSourceIdList(userId, MessageConstant.COMMENT, messageRequest.getIsVisible());
        if (messageList.isEmpty()) {
            return new Page<>();
        }
        List<Long> sourceIdList = messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
        Map<Long, Long> messageMap = messageList.stream().collect(Collectors.toMap(Message::getSourceId, Message::getId));
        // 将消息设置为 INVISIBLE
//        setMessageInVisible(sourceIdList);
        // 查询需要获取的评论
        LambdaQueryWrapper<Comment> commentQueryWrapper = new LambdaQueryWrapper<>();
        commentQueryWrapper.in(Comment::getId, sourceIdList);
        Page<Comment> commentPage = commentService.page(new Page<>(messageRequest.getCurrent(), messageRequest.getPageSize()), commentQueryWrapper);
        List<Comment> commentList = commentPage.getRecords();
        List<MessageCommentVO> messageCommentVOList = commentList.stream().map(comment -> {
            MessageCommentVO messageCommentVO = new MessageCommentVO();
            Long uId = comment.getUid();
            Long parentId = comment.getParentId();
            Integer commentType = comment.getCommentType();
            String content = comment.getContent();
            // 查询评论用户
            User user = userService.getById(uId);
            UserVO userVOInfo = userService.getUserVO(user);
            // 查询被评论内容，如果被评论的是帖子，那就是帖子的标题，如果被评论的是内容，那么就是内容
            String contentBeComment = "";
            if (CommentConstant.POST.equals(commentType)) {
                Post post = postService.getById(parentId);
                contentBeComment = post.getTitle();
            } else {
                Comment parentComment = commentService.getById(parentId);
                contentBeComment = parentComment.getContent();
            }
            Post post = getPostByCommentId(comment.getId());
            BeanUtil.copyProperties(userVOInfo, messageCommentVO);
            messageCommentVO.setCommentContent(content);
            messageCommentVO.setCommentParentId(parentId);
            messageCommentVO.setCommentId(comment.getId());
            messageCommentVO.setCommentParentContent(contentBeComment);
            messageCommentVO.setMessageId(messageMap.get(comment.getId()));
            messageCommentVO.setCreateTime(comment.getCreateTime());
            messageCommentVO.setPostId(post.getId());
            messageCommentVO.setTitle(post.getTitle());
            return messageCommentVO;
        }).collect(Collectors.toList());
        return new Page<MessageCommentVO>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal()).setRecords(messageCommentVOList);
    }

    /**
     * 根据评论ID获取帖子ID
     *
     * @param commentId
     * @return
     */
    private Post getPostByCommentId(Long commentId) {
        while (true) {
            Comment comment = commentService.getById(commentId);
            if (CommentConstant.POST.equals(comment.getCommentType())) {
                Long parentId = comment.getParentId();
                return postService.getById(parentId);
            }
            commentId = comment.getParentId();
        }
    }

    /**
     * 设置消息为 INVISIBLE
     *
     * @param sourceIdList
     */
    public void setMessageInVisible(List<Long> sourceIdList) {
        List<Message> messageList = messageMapper.selectBatchIds(sourceIdList);
        for (Message message : messageList) {
            message.setVisible(MessageConstant.INVISIBLE);
            messageMapper.updateById(message);
        }
    }

    /**
     * 获取各个种类的消息来源ID集合
     *
     * @param userId
     * @param type
     * @return
     */
    @NotNull
    private List<Message> getSourceIdList(Long userId, Integer type, Integer visible) {
        LambdaQueryWrapper<Message> messageQueryWrapper = new LambdaQueryWrapper<>();
        messageQueryWrapper.eq(Message::getUid, userId);
        messageQueryWrapper.eq(Message::getMessageType, type);
        if (MessageConstant.VISIBLE.equals(visible)) {
            messageQueryWrapper.eq(Message::getVisible, visible);
        } else if (MessageConstant.INVISIBLE.equals(visible)) {
            messageQueryWrapper.eq(Message::getVisible, visible);
        }
//        messageQueryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        return messageMapper.selectList(messageQueryWrapper);
//        return messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
    }
}




