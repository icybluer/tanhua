package com.tanhua.dubbo.api.impl;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
//import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //查询今日佳人
    public RecommendUser queryWithMaxScore(Long toUserId) {
        //根据toUserId查询，根据评分score排序，获取第一条
        //构建Criteria
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //构建Query对象
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(1);
        //调用mongoTemplate查询
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public List<RecommendUser> queryRecommendUserList(Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")));
        return mongoTemplate.find(query, RecommendUser.class);
    }

    @Override
    public Map<Long, RecommendUser> queryRecommendUserMap(Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")));
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        return CollUtil.fieldValueMap(recommendUsers, "userId");
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //构建Query对象
        Query query = new Query(Criteria.where("toUserId").is(toUserId));
        //总记录数
        Long count = mongoTemplate.count(query, RecommendUser.class);
        //配置分页参数
        query.skip((page-1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("score")));
        //查询数据列表
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        return new PageResult(page, pagesize, count, recommendUsers);
    }

    @Override
    public RecommendUser getRecommendUser(Long toUserId, Long userId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId)
                .and("userId").is(userId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }
}