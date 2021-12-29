package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.service.TimeLineService;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
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
            .skip((page - 1) * pagesize).limit(pagesize);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, count, movements);
    }

    @Override
    public List<Movement> getFriendsMovements(Long userId, Integer page, Integer pagesize) {
        // 根据用户id查询用户可查看的动态id列表
        Criteria criteria = Criteria.where("friendId").is(userId);
        Query query = Query.query(criteria)
            .with(Sort.by(Sort.Order.desc("created")))
            .skip((page - 1) * pagesize).limit(pagesize);
        List<MovementTimeLine> timelineList = mongoTemplate.find(query, MovementTimeLine.class);
        List<ObjectId> movementIDs = timelineList.stream()
            .map(MovementTimeLine::getMovementId)
            .collect(Collectors.toList());
        // 根据动态id列表查询动态详情
        criteria = Criteria.where("id").in(movementIDs);
        query = Query.query(criteria);
        return mongoTemplate.find(query, Movement.class);
    }

    @Override
    public List<Movement> randomMovements(Integer pagesize) {
        // 创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(Movement.class,Aggregation.sample(pagesize));
        // 调用mongoTemplate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        // 获取统计结果
        return results.getMappedResults();
    }

    @Override
    public Movement findById(String id) {
        return mongoTemplate.findById(id, Movement.class);
    }
}
