package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.common.ResultUtils;
import com.napnap.constant.UserConstant;
import com.napnap.dto.message.MessageReadRequest;
import com.napnap.dto.message.MessageRequest;
import com.napnap.exception.BusinessException;
import com.napnap.service.MessageService;
import com.napnap.utils.SseEmitterUtil;
import com.napnap.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/message")
@Api(tags = "消息管理")
public class MessageController {

    @Resource
    private MessageService messageService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test(HttpServletRequest request){
//        (UserVO)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        SseEmitterUtil.connect(String.valueOf(1));
        SseEmitterUtil.sendMessage("1", "测试消息");
        return "test";
    }

    @ApiOperation("已读消息")
    @LoginCheck
    @PostMapping("/isReadMessage")
    public BaseResponse<Boolean> isReadMessage(@RequestBody MessageReadRequest messageReadRequest){
        if(messageReadRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        List<Long> messageIdList = messageReadRequest.getMessageIdList();
        messageService.setMessageInVisible(messageIdList);
        return ResultUtils.success(true);
    }

    @ApiOperation("获取用户未读消息列表数量")
    @LoginCheck
    @GetMapping("/listMessageCount")
    public BaseResponse<List<Long>> listMessageCount(){
        List<Long> countList = messageService.listMessageCount();
        return ResultUtils.success(countList);
    }

    @ApiOperation("获取关注人列表")
    @LoginCheck
    @PostMapping("/listMessageByFocus")
    public BaseResponse<Page<MessageUserVO>> listMessageByFocus(@RequestBody MessageRequest messageRequest){
        if(messageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageUserVO> userVOPage = messageService.listMessageByFocus(messageRequest);
        return ResultUtils.success(userVOPage);
    }

    @ApiOperation("获取点赞列表")
    @LoginCheck
    @PostMapping("/listMessageByLike")
    public BaseResponse<Page<MessageLikeVO>> listMessageByLike(@RequestBody MessageRequest messageRequest){
        if(messageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageLikeVO> messageLikeVOPage = messageService.listMessageByLike(messageRequest);
        return ResultUtils.success(messageLikeVOPage);
    }

    @ApiOperation("获取收藏列表")
    @LoginCheck
    @PostMapping("/listMessageByCollect")
    public BaseResponse<Page<MessageCollectVO>> listMessageByCollect(@RequestBody MessageRequest messageRequest){
        if(messageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageCollectVO> messageCollectVOPage = messageService.listMessageByCollect(messageRequest);
        return ResultUtils.success(messageCollectVOPage);
    }

    @ApiOperation("获取回复列表")
    @LoginCheck
    @PostMapping("/listMessageByComment")
    public BaseResponse<Page<MessageCommentVO>> listMessageByComment(@RequestBody MessageRequest messageRequest){
        if(messageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageCommentVO> messageCommentVOPage = messageService.listMessageByComment(messageRequest);
        return ResultUtils.success(messageCommentVOPage);
    }
}
