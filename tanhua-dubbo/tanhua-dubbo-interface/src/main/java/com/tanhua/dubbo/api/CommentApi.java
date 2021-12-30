package com.tanhua.dubbo.api;

import com.tanhua.model.dto.CommentDTO;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;

public interface CommentApi {
    List<Comment> getComments(String movementId, CommentType type, Integer page, Integer pagesize);

    Integer save(CommentDTO dto);

    Comment getComment(Long userId, String movementId, Integer commentType);

    Integer delete(Long userId, String movementId, Integer commentType);

    Comment getComment(String commentId);

    Integer likeComment(String commentId);

    Integer dislikeComment(String commentId);
}
