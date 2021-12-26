package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.service.TimeLineService;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class MovementApiImpl implements MovementApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;

    @Override
    public void publish(Movement movement) {
        try {
            movement.setPid(idWorker.getNextId("movement"));
            movement.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movement);

            timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Movement.class);
        query.with(Sort.by(Sort.Order.desc("created")))
                .skip((page - 1) * pagesize)
                .limit(pagesize);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, count, movements);
    }
}
