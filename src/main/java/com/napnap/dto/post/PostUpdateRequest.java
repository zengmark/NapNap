package com.napnap.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PostUpdateRequest implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

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

    private static final long serialVersionUID = 1L;
}
