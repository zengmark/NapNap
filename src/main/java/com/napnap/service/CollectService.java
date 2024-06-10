package com.napnap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.napnap.entity.Collect;

/**
* @author 13123
* @description 针对表【tb_collect(收藏表)】的数据库操作Service
* @createDate 2024-06-09 21:56:28
*/
public interface CollectService extends IService<Collect> {
    boolean changeCollectStatus(long userId, long collectId, int type);

    boolean deleteAllCollectRecord(long collectId, int type);
}
