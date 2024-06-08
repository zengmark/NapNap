package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.common.UserConstant;
import com.napnap.dto.post.PostAddRequest;
import com.napnap.dto.post.PostUpdateRequest;
import com.napnap.entity.Post;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.PostMapper;
import com.napnap.service.PostService;
import com.napnap.vo.PostVO;
import com.napnap.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13123
 * @description 针对表【tb_post(帖子表)】的数据库操作Service实现
 * @createDate 2024-06-08 21:28:02
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private PostMapper postMapper;

    /**
     * 添加一条帖子
     *
     * @param postAddRequest
     * @return
     */
    @Override
    public boolean addPost(PostAddRequest postAddRequest) {
        Post post = new Post();
        String title = postAddRequest.getTitle();
        String content = postAddRequest.getContent();
        List<String> pictures = postAddRequest.getPictures();
        List<String> tag = postAddRequest.getTag();
        post.setTitle(title);
        post.setContent(content);
        if (CollectionUtil.isNotEmpty(pictures)) {
            post.setPictureList(pictures);
        }
        post.setTagList(tag);
        Object loginUser = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserVO userVO = (UserVO) loginUser;
        post.setUserId(userVO.getId());
        postMapper.insert(post);
        return true;
    }

    /**
     * 获取该用户的所有帖子
     *
     * @param pageRequest
     * @return
     */
    @Override
    public Page<PostVO> listAllPostByUser(PageRequest pageRequest) {
        UserVO loginUser = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long userId = loginUser.getId();
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, userId);
        Page<Post> postPage = postMapper.selectPage(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), queryWrapper);
        List<PostVO> postVoList = postPage.getRecords().stream().map(this::getPostVO).collect(Collectors.toList());
        return new Page<PostVO>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal()).setRecords(postVoList);
    }

    /**
     * 更新用户发布的帖子
     *
     * @param postUpdateRequest
     * @return
     */
    @Override
    public Boolean updatePost(PostUpdateRequest postUpdateRequest) {
        Long id = postUpdateRequest.getId();
        Post post = postMapper.selectById(id);
        if(post == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String title = postUpdateRequest.getTitle();
        String content = postUpdateRequest.getContent();
        List<String> pictures = postUpdateRequest.getPictures();
        List<String> tag = postUpdateRequest.getTag();
        post.setTitle(title);
        post.setContent(content);
        post.setPictureList(pictures);
        post.setTagList(tag);
        postMapper.updateById(post);
        return true;
    }

    private PostVO getPostVO(Post post) {
        PostVO postVO = new PostVO();
        BeanUtil.copyProperties(post, postVO);
        postVO.setPictures(post.getPictureList());
        postVO.setTag(post.getTagList());
        return postVO;
    }
}




