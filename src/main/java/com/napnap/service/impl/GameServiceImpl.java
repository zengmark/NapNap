package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.constant.CollectConstant;
import com.napnap.constant.SortConstant;
import com.napnap.constant.UserConstant;
import com.napnap.dto.collect.CollectRequest;
import com.napnap.dto.game.GameScoreRequest;
import com.napnap.dto.game.GameSearchRequest;
import com.napnap.entity.Collect;
import com.napnap.entity.Game;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.GameMapper;
import com.napnap.service.CollectService;
import com.napnap.service.GameService;
import com.napnap.vo.GameVO;
import com.napnap.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13123
 * @description 针对表【tb_game(游戏表)】的数据库操作Service实现
 * @createDate 2024-06-09 18:05:51
 */
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
        implements GameService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private GameMapper gameMapper;

    @Resource
    private CollectService collectService;

    /**
     * 根据搜索条件列举所有游戏
     *
     * @param gameSearchRequest
     * @return
     */
    @Override
    public Page<GameVO> listAllGameBySearch(GameSearchRequest gameSearchRequest) {
        String searchText = gameSearchRequest.getSearchText();
        List<String> tagList = gameSearchRequest.getTagList();
        String sortField = gameSearchRequest.getSortField();
        LambdaQueryWrapper<Game> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(searchText)) {
            queryWrapper.and(wrapper -> wrapper.like(Game::getGameName, searchText).or().like(Game::getGameProfile, searchText));
        }
        if (CollectionUtil.isNotEmpty(tagList)) {
            queryWrapper.and(wrapper -> {
                for (String tag : tagList) {
                    wrapper.or().apply("JSON_CONTAINS(tag, {0})", "\"" + tag + "\"");
                }
            });
        }

        // 组装排序条件
        if (SortConstant.SCORE.equals(sortField)) {
            queryWrapper.orderByDesc(Game::getGameScore);
        }
        if (SortConstant.DOWNLOAD.equals(sortField)) {
            queryWrapper.orderByDesc(Game::getDownloadNum);
        }
        queryWrapper.orderByDesc(Game::getCreateTime);
        Page<Game> gamePage = gameMapper.selectPage(new Page<>(gameSearchRequest.getCurrent(), gameSearchRequest.getPageSize()), queryWrapper);
        List<GameVO> gameVoList = gamePage.getRecords().stream().map(this::getGameVO).collect(Collectors.toList());
        return new Page<GameVO>(gamePage.getCurrent(), gamePage.getSize(), gamePage.getTotal()).setRecords(gameVoList);
    }

    /**
     * 获取用户收藏的所有游戏
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<GameVO> listAllGameByUserCollect(PageRequest pageRequest) {
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 获取收藏表中用户收集的游戏的IDList
        LambdaQueryWrapper<Collect> collectQueryWrapper = new LambdaQueryWrapper<>();
        collectQueryWrapper.eq(Collect::getUid, userId);
        collectQueryWrapper.eq(Collect::getCollectType, CollectConstant.GAME);
        List<Collect> collectList = collectService.list(collectQueryWrapper);
        List<Long> gameIdList = collectList.stream().map(Collect::getCollectedId).collect(Collectors.toList());
        // 根据 gameIdList 集合获取所有游戏
        LambdaQueryWrapper<Game> gameQueryWrapper = new LambdaQueryWrapper<>();
        gameQueryWrapper.in(Game::getId, gameIdList);
        Page<Game> gamePage = gameMapper.selectPage(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), gameQueryWrapper);
        List<Game> gameList = gamePage.getRecords();
        List<GameVO> gameVoList = gameList.stream().map(this::getGameVO).collect(Collectors.toList());
        return new Page<GameVO>(gamePage.getCurrent(), gamePage.getSize(), gamePage.getTotal()).setRecords(gameVoList);
    }

    /**
     * 收藏/取消收藏游戏
     *
     * @param collectRequest
     * @return
     */
    @Transactional
    @Override
    public GameVO collectGame(CollectRequest collectRequest) {
        // collectId 就是 tb_game 表的主键ID
        Long gameId = collectRequest.getCollectId();
        Integer type = collectRequest.getType();
        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = userVO.getId();
        // 收藏/取消收藏记录
        boolean isCollect = collectService.changeCollectStatus(userId, gameId, type);
        Game game = gameMapper.selectById(gameId);
        if (isCollect) {
            game.setCollectNum(game.getCollectNum() + 1);
        } else {
            game.setCollectNum(game.getCollectNum() - 1);
        }
        gameMapper.updateById(game);
        return getGameVO(game);
    }

    /**
     * 给游戏评分
     *
     * @param gameScoreRequest
     * @return
     */
    @Override
    public GameVO scoreGame(GameScoreRequest gameScoreRequest) {
        Long gameId = gameScoreRequest.getGameId();
        Integer score = gameScoreRequest.getScore();
        Game game = gameMapper.selectById(gameId);
        if(game == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评分的游戏不存在");
        }
        BigDecimal gameScore = game.getGameScore();
        BigDecimal addScore = new BigDecimal(score);
        Long gameNum = game.getGameNum();
        BigDecimal divideScore = gameScore.multiply(new BigDecimal(gameNum)).add(addScore).divide(new BigDecimal(gameNum + 1), 1, BigDecimal.ROUND_HALF_UP);
        game.setGameScore(divideScore);
        game.setGameNum(gameNum + 1);
        gameMapper.updateById(game);
        return getGameVO(game);
    }

    /**
     * 游戏数据脱敏
     * @param game
     * @return
     */
    private GameVO getGameVO(Game game) {
        GameVO gameVO = new GameVO();
        BeanUtil.copyProperties(game, gameVO);
        gameVO.setGameIcon(game.getGameIconList());
        gameVO.setTag(game.getTagList());
        return gameVO;
    }
}
