package com.tanhua.server.service;

import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.CommentDTO;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 查询全部评论
     */
    public PageResult getComments(String movementId, Integer page, Integer pagesize) {
        // 根据动态id查询动态的评论列表
        List<Comment> commentList = commentApi.getComments(movementId, CommentType.COMMENT, page, pagesize);
        // 判断列表是否为空
        if (CollectionUtils.isEmpty(commentList)) {
            return new PageResult();
        }
        // 根据评论的评论者id查询评论者的用户信息
        // 获取评论者id列表
        List<Long> userIds = commentList.stream()
                .map(Comment::getUserId)
                .collect(Collectors.toList());
        // 根据id列表查询用户详细信息列表
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
        // 封装VO对象
        List<CommentVo> vos = commentList.stream()
                .filter(comment -> userInfoMap.get(comment.getUserId()) != null)
                .map(comment -> CommentVo.init(userInfoMap.get(comment.getUserId()), comment))
                .peek(commentVo -> {
                    String key = Constants.MOVEMENTS_INTERACT_KEY + commentVo.getId();
                    String hashKey = Constants.COMMENT_LIKE_HASHKEY + UserHolder.getUserId();
                    Boolean hasLike = redisTemplate.opsForHash().hasKey(key, hashKey);
                    if (hasLike) {
                        commentVo.setHasLiked(1);
                    }
                })
                .collect(Collectors.toList());
        // 封装分页结果
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 发布评论
     */
    public Integer publishComment(CommentDTO dto) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        dto.setUserId(userId);
        dto.setCommentType(CommentType.COMMENT.getType());
        // 调用api
        return commentApi.save(dto);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    public Integer likeComment(String commentId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否点赞
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;
        String hashKey = Constants.COMMENT_LIKE_HASHKEY + userId;
        Boolean hasLike = redisTemplate.opsForHash().hasKey(key, hashKey);
        if (hasLike) {
            // 已点赞，查询评论，返回点赞数
            return commentApi.getComment(commentId).getLikeCount();
        }
        // 未点赞，增加点赞数
        Integer likeCount = commentApi.likeComment(commentId);
        // redis中保存点赞状态
        redisTemplate.opsForHash().put(key, hashKey, "1");
        return likeCount;
    }

    public Integer dislikeComment(String commentId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 查询用户是否点赞
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;
        String hashKey = Constants.COMMENT_LIKE_HASHKEY + userId;
        Boolean hasLike = redisTemplate.opsForHash().hasKey(key, hashKey);
        if (!hasLike) {
            // 未点赞，查询评论，返回点赞数
            return commentApi.getComment(commentId).getLikeCount();
        }
        // 已点赞，减少点赞数
        Integer likeCount = commentApi.dislikeComment(commentId);
        // 移除redis中的点赞状态
        redisTemplate.opsForHash().delete(key, hashKey);
        return likeCount;
    }
}
