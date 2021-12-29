package com.tanhua.dubbo.api;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.mongo.Movement;

import java.util.List;

public interface MovementApi {
    void publish(Movement movement);

    PageResult findByUserId(Long userId, Integer page, Integer pagesize);

    List<Movement> getFriendsMovements(Long userId, Integer page, Integer pagesize);

    List<Movement> randomMovements(Integer pagesize);

    Movement findById(String id);
}
