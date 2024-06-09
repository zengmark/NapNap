package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GameVO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 游戏简介
     */
    private String gameProfile;

    /**
     * 游戏图片
     */
    private List<String> gameIcon;

    /**
     * 标签
     */
    private List<String> tag;

    /**
     * 评分
     */
    private BigDecimal gameScore;

    /**
     * 游戏大小
     */
    private BigDecimal gameSize;

    /**
     * 游戏链接
     */
    private String gameUrl;

    /**
     * 收藏数
     */
    private Long collectNum;

    /**
     * 下载量
     */
    private Long downloadNum;

    private static final long serialVersionUID = 1L;
}
