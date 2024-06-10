package com.napnap.dto.game;

import com.napnap.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GameSearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索的内容应该是根据标题和内容进行搜索
     */
    private String searchText;

    /**
     * 标签列表
     */
    private List<String> tagList;

    private static final long serialVersionUID = 1L;
}
