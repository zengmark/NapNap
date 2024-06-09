package com.napnap.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.common.PageRequest;
import com.napnap.constant.CollectConstant;
import com.napnap.constant.SortConstant;
import com.napnap.constant.UserConstant;
import com.napnap.dto.collect.CollectRequest;
import com.napnap.dto.post.PostAddRequest;
import com.napnap.dto.post.PostSearchRequest;
import com.napnap.dto.post.PostUpdateRequest;
import com.napnap.entity.Post;
import com.napnap.exception.BusinessException;
import com.napnap.mapper.PostMapper;
import com.napnap.service.CollectService;
import com.napnap.service.PostService;
import com.napnap.vo.PostVO;
import com.napnap.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private CollectService collectService;

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
        if (post == null) {
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

    /**
     * 根据搜索条件搜索帖子（仅支持模糊查询和 tag 匹配）
     *
     * @param postSearchRequest
     * @return
     */
    @Override
    public Page<PostVO> listAllPostBySearch(PostSearchRequest postSearchRequest) {
        String searchText = postSearchRequest.getSearchText();
        List<String> tagList = postSearchRequest.getTagList();
        String sortField = postSearchRequest.getSortField();
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(searchText)) {
            queryWrapper.and(wrapper -> wrapper.like(Post::getTitle, searchText).or().like(Post::getContent, searchText));
        }
        if (CollectionUtil.isNotEmpty(tagList)) {
            queryWrapper.and(wrapper -> {
                for (String tag : tagList) {
                    wrapper.or().apply("JSON_CONTAINS(tag, {0})", "\"" + tag + "\"");
                }
            });
        }

        // 组装排序条件
        if (SortConstant.LIKE.equals(sortField)) {
            queryWrapper.orderByDesc(Post::getLikes);
        }
        if (SortConstant.COLLECT.equals(sortField)) {
            queryWrapper.orderByDesc(Post::getCollectNum);
        }
        queryWrapper.orderByDesc(Post::getCreateTime);

        // 分页查询
        Page<Post> postPage = postMapper.selectPage(new Page<>(postSearchRequest.getCurrent(), postSearchRequest.getPageSize()), queryWrapper);
        List<PostVO> postVoList = postPage.getRecords().stream().map(this::getPostVO).collect(Collectors.toList());
        return new Page<PostVO>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal()).setRecords(postVoList);
    }

    /**
     * 更改帖子点赞数
     *
     * @param postId
     * @param num
     */
    @Override
    public void changePostLikes(long postId, long num) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "点赞失败，帖子不存在");
        }
        post.setLikes(post.getLikes() + num);
        postMapper.updateById(post);
    }

    /**
     * 更改帖子收藏数
     *
     * @param postId
     * @param num
     */
    @Override
    public void changePostCollectNum(long postId, long num) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "收藏失败，帖子不存在");
        }
        post.setCollectNum(post.getCollectNum() + num);
        postMapper.updateById(post);
    }

    /**
     * 收藏/取消收藏帖子
     *
     * @param collectRequest
     * @return
     */
    @Transactional
    @Override
    public PostVO collectPost(CollectRequest collectRequest) {
        // collectId 就是 tb_post 表的主键ID
        Long collectId = collectRequest.getCollectId();
        Integer type = collectRequest.getType();
        Object loginUser = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserVO userVO = (UserVO) loginUser;
        Long userId = userVO.getId();
        // 收藏/取消收藏记录
        boolean isCollect = collectService.changeCollectStatus(userId, collectId, type);
        Post post = postMapper.selectById(collectId);
        // 如果是收藏
        if (isCollect) {
            post.setCollectNum(post.getCollectNum() + 1);
        } else {
            post.setCollectNum(post.getCollectNum() - 1);
        }
        postMapper.updateById(post);
        return getPostVO(post);
    }

    /**
     * 帖子数据脱敏
     *
     * @param post
     * @return
     */
    private PostVO getPostVO(Post post) {
        PostVO postVO = new PostVO();
        BeanUtil.copyProperties(post, postVO);
        postVO.setPictures(post.getPictureList());
        postVO.setTag(post.getTagList());
        return postVO;
    }
}




