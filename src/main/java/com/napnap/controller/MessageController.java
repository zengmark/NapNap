package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.common.ResultUtils;
import com.napnap.exception.BusinessException;
import com.napnap.service.MessageService;
import com.napnap.vo.MessageCollectVO;
import com.napnap.vo.MessageCommentVO;
import com.napnap.vo.MessageLikeVO;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api(tags = "消息管理")
public class MessageController {

    @Resource
    private MessageService messageService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @ApiOperation("获取用户消息列表数量")
    @LoginCheck
    @GetMapping("/listMessageCount")
    public BaseResponse<List<Long>> listMessageCount(){
        List<Long> countList = messageService.listMessageCount();
        return ResultUtils.success(countList);
    }

    @ApiOperation("获取关注人列表")
    @LoginCheck
    @PostMapping("/listMessageByFocus")
    public BaseResponse<Page<UserVO>> listMessageByFocus(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<UserVO> userVOPage = messageService.listMessageByFocus(pageRequest);
        return ResultUtils.success(userVOPage);
    }

    @ApiOperation("获取点赞列表")
    @LoginCheck
    @PostMapping("/listMessageByLike")
    public BaseResponse<Page<MessageLikeVO>> listMessageByLike(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageLikeVO> messageLikeVOPage = messageService.listMessageByLike(pageRequest);
        return ResultUtils.success(messageLikeVOPage);
    }

    @ApiOperation("获取收藏列表")
    @LoginCheck
    @PostMapping("/listMessageByCollect")
    public BaseResponse<Page<MessageCollectVO>> listMessageByCollect(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageCollectVO> messageCollectVOPage = messageService.listMessageByCollect(pageRequest);
        return ResultUtils.success(messageCollectVOPage);
    }

    @ApiOperation("获取回复列表")
    @LoginCheck
    @PostMapping("/listMessageByComment")
    public BaseResponse<Page<MessageCommentVO>> listMessageByComment(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<MessageCommentVO> messageCommentVOPage = messageService.listMessageByComment(pageRequest);
        return ResultUtils.success(messageCommentVOPage);
    }
}
