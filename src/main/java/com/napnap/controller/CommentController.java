package com.napnap.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.ResultUtils;
import com.napnap.dto.comment.CommentAddRequest;
import com.napnap.dto.comment.CommentDeleteRequest;
import com.napnap.dto.comment.CommentQueryRequest;
import com.napnap.entity.Comment;
import com.napnap.exception.BusinessException;
import com.napnap.service.CommentService;
import com.napnap.vo.CommentUnderPostVO;
import com.napnap.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论管理")
public class CommentController {

    @Resource
    private CommentService commentService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @ApiOperation("根据评论ID获取评论")
    @PostMapping("/getCommentById")
    public BaseResponse<CommentVO> getCommentById(@RequestBody CommentQueryRequest commentQueryRequest){
        if(commentQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Comment comment = commentService.getById(commentQueryRequest.getPostId());
        CommentVO commentVO = new CommentVO();
        BeanUtil.copyProperties(comment, commentVO);
        commentVO.setPicture(comment.getPictureList());
        return ResultUtils.success(commentVO);
    }

    @ApiOperation("添加评论")
    @LoginCheck
    @PostMapping("/addComment")
    public BaseResponse<Boolean> addComment(@RequestBody CommentAddRequest commentAddRequest){
        if(commentAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        boolean flag = commentService.addComment(commentAddRequest);
        return ResultUtils.success(flag);
    }

    @ApiOperation("获取帖子下的所有评论及其子评论")
    @PostMapping("/listAllCommentUnderPost")
    public BaseResponse<Page<CommentUnderPostVO>> listAllCommentUnderPost(@RequestBody CommentQueryRequest commentQueryRequest){
        if(commentQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<CommentUnderPostVO> commentUnderPostVOPage = commentService.listAllCommentUnderPost(commentQueryRequest);
        return ResultUtils.success(commentUnderPostVOPage);
    }

    @ApiOperation("删除评论")
    @LoginCheck
    @PostMapping("/deleteCommentById")
    public BaseResponse<Boolean> deleteCommentById(@RequestBody CommentDeleteRequest commentDeleteRequest){
        if(commentDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        boolean flag = commentService.deleteCommentById(commentDeleteRequest);
        return ResultUtils.success(flag);
    }
}
