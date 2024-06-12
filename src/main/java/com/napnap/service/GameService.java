package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.common.PageRequest;
import com.napnap.dto.collect.CollectRequest;
import com.napnap.dto.game.GameScoreRequest;
import com.napnap.dto.game.GameSearchRequest;
import com.napnap.entity.Game;
import com.napnap.vo.GameVO;

/**
* @author 13123
* @description 针对表【tb_game(游戏表)】的数据库操作Service
* @createDate 2024-06-09 18:05:51
*/
public interface GameService extends IService<Game> {

    Page<GameVO> listAllGameBySearch(GameSearchRequest gameSearchRequest);

    Page<GameVO> listAllGameByUserCollect(PageRequest pageRequest);

    GameVO collectGame(CollectRequest collectRequest);

    GameVO scoreGame(GameScoreRequest gameScoreRequest);
}
