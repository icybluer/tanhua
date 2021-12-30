package com.tanhua.model.domain;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
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