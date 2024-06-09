package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.constant.SortConstant;
import com.napnap.dto.game.GameSearchRequest;
import com.napnap.entity.Game;
import com.napnap.entity.Post;
import com.napnap.mapper.GameMapper;
import com.napnap.service.GameService;
import com.napnap.vo.GameVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 13123
* @description 针对表【tb_game(游戏表)】的数据库操作Service实现
* @createDate 2024-06-09 18:05:51
*/
@Service
public class GameServiceImpl extends ServiceImpl<GameMapper, Game>
    implements GameService{

    @Resource
    private GameMapper gameMapper;

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
        List<GameVO> gameVoList = gamePage.getRecords().stream().map(this::getPageVO).collect(Collectors.toList());
        return new Page<GameVO>(gamePage.getCurrent(), gamePage.getSize(), gamePage.getTotal()).setRecords(gameVoList);
    }

    private GameVO getPageVO(Game game){
        GameVO gameVO = new GameVO();
        BeanUtil.copyProperties(game, gameVO);
        gameVO.setGameIcon(game.getGameIconList());
        gameVO.setTag(game.getTagList());
        return gameVO;
    }
}




