package com.napnap.dto.game;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameScoreRequest implements Serializable {

    /**
     * 被评分的游戏ID
     */
    private Long gameId;

    /**
     * 用户评分
     */
    private Integer score;

    private static final long serialVersionUID = 1L;
}
