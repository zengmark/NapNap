package com.napnap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.napnap.common.ErrorCode;
import com.napnap.constant.CollectConstant;
import com.napnap.entity.Collect;
import com.napnap.exception.BusinessException;
import com.napnap.service.CollectService;
import com.napnap.mapper.CollectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 13123
* @description 针对表【tb_collect(收藏表)】的数据库操作Service实现
* @createDate 2024-06-09 21:56:28
*/
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect>
    implements CollectService{

    @Resource
    private CollectMapper collectMapper;


    /**
     * 一条收藏记录，返回值为 true 代表是收藏，返回值为 false 代表是取消收藏
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
        queryWrapper.eq(Collect::getType, type);
        Collect collect = collectMapper.selectOne(queryWrapper);
        // 存在记录的情况下，将其逻辑删除
        if(collect != null){
//            // 将其逻辑删除，并且判断其是帖子还是游戏，如果是帖子，那么帖子关注数 - 1，如果是游戏，游戏收藏数 - 1
//            if(CollectConstant.POST.equals(type)){
//
//            } else if(CollectConstant.GAME.equals(type)){
//
//            } else {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有该类型的收藏数据");
//            }
            collectMapper.deleteById(collect);
            return false;
        }
        // 不存在，插入一条收藏记录
        collect = new Collect();
        collect.setUid(userId);
        collect.setCollectedId(collectId);
        collect.setType(type);
        collectMapper.insert(collect);
        return true;
    }
}




