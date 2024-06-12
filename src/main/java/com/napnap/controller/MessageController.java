package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ResultUtils;
import com.napnap.service.MessageService;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public BaseResponse<Page<UserVO>> listMessageByFocus(){

        return null;
    }

    @ApiOperation("获取点赞列表")
    @LoginCheck
    @PostMapping("/listMessageByLike")
    public BaseResponse<Page<UserVO>> listMessageByLike(){

        return null;
    }

    @ApiOperation("获取收藏列表")
    @LoginCheck
    @PostMapping("/listMessageByCollect")
    public BaseResponse<Page<UserVO>> listMessageByCollect(){

        return null;
    }

    @ApiOperation("获取回复列表")
    @LoginCheck
    @PostMapping("/listMessageByComment")
    public BaseResponse<Page<UserVO>> listMessageByComment(){

        return null;
    }
}
