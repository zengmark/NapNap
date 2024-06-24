package com.napnap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.common.ResultUtils;
import com.napnap.constant.CollectConstant;
import com.napnap.constant.UserConstant;
import com.napnap.dto.collect.CollectRequest;
import com.napnap.dto.game.GameAddRequest;
import com.napnap.dto.game.GameScoreRequest;
import com.napnap.dto.game.GameSearchRequest;
import com.napnap.dto.post.PostOtherRequest;
import com.napnap.entity.Collect;
import com.napnap.entity.Game;
import com.napnap.exception.BusinessException;
import com.napnap.service.CollectService;
import com.napnap.service.GameService;
import com.napnap.vo.GameVO;
import com.napnap.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/game")
@Api(tags = "游戏管理")
public class GameController {

    @Resource
    private GameService gameService;

    @Resource
    private CollectService collectService;

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

    @ApiOperation("获取用户收藏的游戏列表")
    @LoginCheck
    @PostMapping("/listAllGameByUserCollect")
    public BaseResponse<Page<GameVO>> listAllGameByUserCollect(@RequestBody PostOtherRequest postOtherRequest){
        if(postOtherRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Page<GameVO> gameVOPage = gameService.listAllGameByUserCollect(postOtherRequest);
        return ResultUtils.success(gameVOPage);
    }

    @ApiOperation("收藏/取消收藏游戏")
    @LoginCheck
    @PostMapping("/collectGame")
    public BaseResponse<GameVO> collectGame(@RequestBody CollectRequest collectRequest){
        if(collectRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        GameVO gameVO = gameService.collectGame(collectRequest);
        return ResultUtils.success(gameVO);
    }

    @ApiOperation("获取游戏是否收藏状态")
    @LoginCheck
    @PostMapping("/getCollectGameStatus")
    public BaseResponse<Boolean> getCollectGameStatus(@RequestBody CollectRequest collectRequest, HttpServletRequest request){
        if(collectRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUid, userId);
        queryWrapper.eq(Collect::getCollectedId, collectRequest.getCollectId());
        queryWrapper.eq(Collect::getCollectType, CollectConstant.GAME);
        Collect collect = collectService.getOne(queryWrapper);
        return ResultUtils.success(collect != null);
    }

    @ApiOperation("为游戏评分")
    @LoginCheck
    @PostMapping("/scoreGame")
    public BaseResponse<GameVO> scoreGame(@RequestBody GameScoreRequest gameScoreRequest){
        if(gameScoreRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        GameVO gameVO = gameService.scoreGame(gameScoreRequest);
        return ResultUtils.success(gameVO);
    }

    @ApiOperation("添加游戏")
    @PostMapping("/addGame")
    public BaseResponse<GameVO> addGame(@RequestBody GameAddRequest gameAddRequest){
        if(gameAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        GameVO gameVO = gameService.addGame(gameAddRequest);
        return ResultUtils.success(gameVO);
    }
}
