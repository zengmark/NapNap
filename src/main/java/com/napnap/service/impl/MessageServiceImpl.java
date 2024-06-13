package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.constant.CommentConstant;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.entity.*;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.MessageMapper;
import com.napnap.service.*;
import com.napnap.vo.MessageCollectVO;
import com.napnap.vo.MessageCommentVO;
import com.napnap.vo.MessageLikeVO;
import com.napnap.vo.UserVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
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
     * @param pageRequest
     * @return
     */
    @Override
    public Page<UserVO> listMessageByFocus(PageRequest pageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是粉丝表的主键ID
        List<Long> sourceIdList = getSourceIdList(userId, MessageConstant.FOCUS);
        // 查询用户ID
        LambdaQueryWrapper<Follower> followerQueryWrapper = new LambdaQueryWrapper<>();
        followerQueryWrapper.in(Follower::getId, sourceIdList);
        Page<Follower> followerPage = followerService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), followerQueryWrapper);
        List<Follower> followerList = followerPage.getRecords();
        List<UserVO> userVOList = followerList.stream().map(follower -> {
            Long uId = follower.getUid();
            // 获取到关注者，将其封装为一个UserVO对象
            User user = userService.getById(uId);
            return userService.getUserVO(user);
        }).collect(Collectors.toList());
        return new Page<UserVO>(followerPage.getCurrent(), followerPage.getSize(), followerPage.getTotal()).setRecords(userVOList);
    }

    /**
     * 获取点赞列表的未读消息
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<MessageLikeVO> listMessageByLike(PageRequest pageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是点赞表的主键ID
        List<Long> sourceIdList = getSourceIdList(userId, MessageConstant.LIKE);
        // 获取点赞列表的ID
        LambdaQueryWrapper<Like> likeQueryWrapper = new LambdaQueryWrapper<>();
        likeQueryWrapper.in(Like::getId, sourceIdList);
        Page<Like> likePage = likeService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), likeQueryWrapper);
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
            return messageLikeVO;
        }).collect(Collectors.toList());
        return new Page<MessageLikeVO>(likePage.getCurrent(), likePage.getSize(), likePage.getTotal()).setRecords(messageLikeVOList);
    }

    /**
     * 获取收藏列表的未读消息
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<MessageCollectVO> listMessageByCollect(PageRequest pageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是收藏表的主键ID
        List<Long> sourceIdList = getSourceIdList(userId, MessageConstant.COLLECT);
        // 查询用户ID
        LambdaQueryWrapper<Collect> collectQueryWrapper = new LambdaQueryWrapper<>();
        collectQueryWrapper.in(Collect::getId, sourceIdList);
        Page<Collect> collectPage = collectService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), collectQueryWrapper);
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
            return messageCollectVO;
        }).collect(Collectors.toList());
        return new Page<MessageCollectVO>(collectPage.getCurrent(), collectPage.getSize(), collectPage.getTotal()).setRecords(messageLikeVOList);
    }

    /**
     * 获取评论列表的未读消息
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<MessageCommentVO> listMessageByComment(PageRequest pageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取消息来源ID，这里的消息来源ID就是评论表的主键ID
        List<Long> sourceIdList = getSourceIdList(userId, MessageConstant.COMMENT);
        LambdaQueryWrapper<Comment> commentQueryWrapper = new LambdaQueryWrapper<>();
        commentQueryWrapper.in(Comment::getId, sourceIdList);
        Page<Comment> commentPage = commentService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), commentQueryWrapper);
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
            if(CommentConstant.POST.equals(commentType)){
                Post post = postService.getById(parentId);
                contentBeComment = post.getTitle();
            } else {
                Comment parentComment = commentService.getById(parentId);
                contentBeComment = parentComment.getContent();
            }
            BeanUtil.copyProperties(userVOInfo, messageCommentVO);
            messageCommentVO.setCommentContent(content);
            messageCommentVO.setCommentParentId(parentId);
            messageCommentVO.setCommentId(comment.getId());
            messageCommentVO.setCommentParentContent(contentBeComment);
            return messageCommentVO;
        }).collect(Collectors.toList());
        return new Page<MessageCommentVO>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal()).setRecords(messageCommentVOList);
    }

    /**
     * 获取各个种类的消息来源ID集合
     *
     * @param userId
     * @param like
     * @return
     */
    @NotNull
    private List<Long> getSourceIdList(Long userId, Integer like) {
        LambdaQueryWrapper<Message> messageQueryWrapper = new LambdaQueryWrapper<>();
        messageQueryWrapper.eq(Message::getUid, userId);
        messageQueryWrapper.eq(Message::getMessageType, like);
        messageQueryWrapper.eq(Message::getVisible, MessageConstant.VISIBLE);
        List<Message> messageList = messageMapper.selectList(messageQueryWrapper);
        return messageList.stream().map(Message::getSourceId).collect(Collectors.toList());
    }
}




