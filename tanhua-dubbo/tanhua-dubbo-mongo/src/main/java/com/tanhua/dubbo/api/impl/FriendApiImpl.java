package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        addFriend(userId, friendId);
        addFriend(friendId, userId);
    }

    @Override
    public List<Friend> getFriends(Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Friend.class);
    }

    private void addFriend(Long userId, Long friendId) {
        // 查询好友表
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("friendId").is(friendId);
        Query query = Query.query(criteria);
        boolean existed = mongoTemplate.exists(query, Friend.class);
        // 好友关系不存在则保存好友关系到mongodb
        if (!existed) {
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }
}
