package com.tanhua.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDTO implements Serializable {
    private Long userId;
    private String movementId;
    private String comment;
    private Integer commentType;
}
