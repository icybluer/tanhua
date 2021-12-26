package com.tanhua.dubbo.api;

import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.mongo.Movement;

public interface MovementApi {
    void publish(Movement movement);

    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}
