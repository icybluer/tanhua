package com.tanhua.model.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVo {
    private Boolean isNew;
    private String token;
}
