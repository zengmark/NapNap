package com.napnap.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PostVO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 发帖用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片地址
     */
    private List<String> pictures;

    /**
     * 标签
     */
    private List<String> tag;

    /**
     * 点赞数
     */
    private Long likes;

    /**
     * 帖子收藏数
     */
    private Long collectNum;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
