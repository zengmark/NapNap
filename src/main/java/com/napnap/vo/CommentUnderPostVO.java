package com.napnap.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CommentUnderPostVO extends CommentVO implements Serializable {

    private List<CommentUnderPostVO> replies;

    private static final long serialVersionUID = 1L;
}
