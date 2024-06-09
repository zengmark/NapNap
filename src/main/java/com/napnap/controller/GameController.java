package com.napnap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.ResultUtils;
import com.napnap.dto.game.GameSearchRequest;
import com.napnap.exception.BusinessException;
import com.napnap.service.GameService;
import com.napnap.vo.GameVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/game")
@Api("游戏管理")
public class GameController {

    @Resource
    private GameService gameService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @ApiOperation("获取游戏列表")
    @PostMapping("/listAllGameBySearch")
    public BaseResponse<Page<GameVO>> listAllGameBySearch(@RequestBody GameSearchRequest gameSearchRequest){
        if(gameSearchRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<GameVO> gameVOPage = gameService.listAllGameBySearch(gameSearchRequest);
        return ResultUtils.success(gameVOPage);
    }
}
