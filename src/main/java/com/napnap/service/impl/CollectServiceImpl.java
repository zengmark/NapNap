package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.constant.CollectConstant;
import com.napnap.constant.MessageConstant;
import com.napnap.constant.UserConstant;
import com.napnap.entity.Collect;
import com.napnap.entity.Post;
import com.napnap.mapper.CollectMapper;
import com.napnap.mapper.PostMapper;
import com.napnap.service.CollectService;
import com.napnap.service.MessageService;
import com.napnap.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 13123
 * @description 针对表【tb_collect(收藏表)】的数据库操作Service实现
 * @createDate 2024-06-09 21:56:28
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect>
        implements CollectService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private CollectMapper collectMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private PostMapper postMapper;

    /**
     * 一条收藏记录，返回值为 true 代表是收藏，返回值为 false 代表是取消收藏
     *
     * @param userId
     * @param collectId
     * @param type
     * @return
     */
    @Override
    public boolean changeCollectStatus(long userId, long collectId, int type) {
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getUid, userId);
        queryWrapper.eq(Collect::getCollectedId, collectId);
        queryWrapper.eq(Collect::getCollectType, type);
        Collect collect = collectMapper.selectOne(queryWrapper);
        // 存在记录的情况下，将其逻辑删除
        if (collect != null) {
//            // 将其逻辑删除，并且判断其是帖子还是游戏，如果是帖子，那么帖子关注数 - 1，如果是游戏，游戏收藏数 - 1
//            if(CollectConstant.POST.equals(type)){
//
//            } else if(CollectConstant.GAME.equals(type)){
//
//            } else {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有该类型的收藏数据");
//            }
            if (CollectConstant.POST.equals(type)) {
                Post post = postMapper.selectById(collectId);
                userId = post.getUserId();
            }
            messageService.deleteMessage(collect.getId(), MessageConstant.COLLECT, userId);
            collectMapper.deleteById(collect);
            return false;
        }
        // 不存在，插入一条收藏记录
        collect = new Collect();
        collect.setUid(userId);
        collect.setCollectedId(collectId);
        collect.setCollectType(type);
        collectMapper.insert(collect);
        if (CollectConstant.POST.equals(type)) {
            Post post = postMapper.selectById(collectId);
            userId = post.getUserId();
        }
        messageService.addMessage(collect.getId(), MessageConstant.COLLECT, userId);
        return true;
    }

    /**
     * 根据 collectId 和 type 将所有记录删除
     *
     * @param collectId
     * @param type
     * @return
     */
    @Override
    public boolean deleteAllCollectRecord(long collectId, int type) {
//        UserVO userVO = (UserVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
//        Long userId = userVO.getId();
        Long userId = null;
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collect::getCollectedId, collectId);
        queryWrapper.eq(Collect::getCollectType, type);
        List<Collect> collectList = collectMapper.selectList(queryWrapper);
        for (Collect collect : collectList) {
//            userId = collect.getUid();
            if (CollectConstant.POST.equals(collect.getCollectType())) {
                Post post = postMapper.selectById(collect.getCollectedId());
                userId = post.getUserId();
                messageService.deleteMessage(collect.getId(), MessageConstant.COLLECT, userId);
            }
        }
        collectMapper.delete(queryWrapper);
        return true;
    }


}




