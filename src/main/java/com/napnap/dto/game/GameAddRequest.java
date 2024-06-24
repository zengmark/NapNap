package com.napnap.dto.game;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GameAddRequest implements Serializable {

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
    private String gameIcon;

    /**
     * 游戏标签
     */
    private List<String> tag;

    /**
     * 游戏大小
     */
    private BigDecimal gameSize;

    /**
     * 游戏链接
     */
    private List<String> gameUrl;

    private static final long serialVersionUID = 1L;
}
