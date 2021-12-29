package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.model.dto.CommentDTO;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Comment> getComments(String movementId, CommentType type, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(type.getType());
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("created")))
                .skip((page - 1) * pagesize).limit(pagesize);
        return mongoTemplate.find(query, Comment.class);
    }

    @Override
    public Integer save(CommentDTO dto) {
        // 查询动态
        Movement movement = mongoTemplate.findById(dto.getMovementId(), Movement.class);
        // 判断动态是否存在
        if (movement == null) {
            throw new RuntimeException();
        }
        // 封装互动对象
        Comment comment = new Comment();
        comment.setPublishId(movement.getId());
        comment.setCommentType(dto.getCommentType());
        comment.setContent(dto.getComment());
        comment.setUserId(dto.getUserId());
        comment.setPublishUserId(movement.getUserId());
        comment.setCreated(System.currentTimeMillis());
        comment.setLikeCount(0);
        // 保存到数据库
        mongoTemplate.save(comment);
        // 更新动态状态
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else {
            update.inc("loveCount", 1);
        }
        // 设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        // 获取最新的评论数量，并返回
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public Comment hasComment(Long userId, String movementId, Integer commentType) {
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Comment.class);
    }

    @Override
    public Integer delete(Long userId, String movementId, Integer commentType) {
        // 查询动态
        Movement movement = mongoTemplate.findById(movementId, Movement.class);
        // 判断动态是否存在
        if (movement == null) {
            throw new RuntimeException();
        }
        // 删除互动数据
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType);
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        // 更新动态状态
        query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        Update update = new Update();
        if (commentType == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (commentType == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            update.inc("loveCount", -1);
        }
        // 设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        // 获取最新的评论数量，并返回
        return modify.statisCount(commentType);
    }
}
