package com.tanhua.model.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BasePojo {

    private Long id;
    private Long userId;
    //问题内容
    private String txt;

}