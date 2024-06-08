package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.common.*;
import com.napnap.dto.post.PostAddRequest;
import com.napnap.dto.post.PostUpdateRequest;
import com.napnap.exception.BusinessException;
import com.napnap.service.PostService;
import com.napnap.vo.PostVO;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/post")
@Api(tags = "帖子管理")
public class PostController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private PostService postService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @ApiOperation("添加帖子")
    @LoginCheck
    @PostMapping("/addPost")
    public BaseResponse<Boolean> addPost(@RequestBody PostAddRequest postAddRequest) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        String title = postAddRequest.getTitle();
        String content = postAddRequest.getContent();
        List<String> tag = postAddRequest.getTag();
        if(StringUtils.isAnyEmpty(title, content) || tag.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题/内容/标签不能为空");
        }
        boolean flag = postService.addPost(postAddRequest);
        return ResultUtils.success(flag);
    }

    @ApiOperation("获取该用户发过的所有帖子")
    @LoginCheck
    @PostMapping("/listAllPostByUser")
    public BaseResponse<Page<PostVO>> listAllPostByUser(@RequestBody PageRequest pageRequest){
        Page<PostVO> postPage = postService.listAllPostByUser(pageRequest);
        return ResultUtils.success(postPage);
    }

    @ApiOperation("修改用户的帖子内容")
    @LoginCheck
    @PutMapping("/updatePost")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest){
        if(postUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        return ResultUtils.success(postService.updatePost(postUpdateRequest));
    }

}
