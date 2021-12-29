package com.tanhua.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private Boolean isNew;
    private String token;
}
