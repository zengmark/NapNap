package com.napnap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.common.PageRequest;
import com.napnap.dto.collect.CollectRequest;
import com.napnap.dto.post.PostAddRequest;
import com.napnap.dto.post.PostSearchRequest;
import com.napnap.dto.post.PostUpdateRequest;
import com.napnap.entity.Post;
import com.napnap.vo.PostVO;

/**
* @author 13123
* @description 针对表【tb_post(帖子表)】的数据库操作Service
* @createDate 2024-06-08 21:28:02
*/
public interface PostService extends IService<Post> {

    boolean addPost(PostAddRequest postAddRequest);

    Page<PostVO> listAllPostByUser(PageRequest pageRequest);

    Boolean updatePost(PostUpdateRequest postUpdateRequest);

    Page<PostVO> listAllPostBySearch(PostSearchRequest postSearchRequest);

    void changePostLikes(long postId, long num);

    void changePostCollectNum(long postId, long num);

    PostVO collectPost(CollectRequest collectRequest);
}
