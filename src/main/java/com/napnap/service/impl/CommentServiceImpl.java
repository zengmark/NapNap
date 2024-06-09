package com.napnap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.entity.Comment;
import com.napnap.service.CommentService;
import com.napnap.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 13123
* @description 针对表【tb_comment(评论表)】的数据库操作Service实现
* @createDate 2024-06-09 21:56:28
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




