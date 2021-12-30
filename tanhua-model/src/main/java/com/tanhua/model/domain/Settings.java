package com.tanhua.model.domain;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settings extends BasePojo {

    private Long id;
    private Long userId;
    private Boolean likeNotification;
    private Boolean pinglunNotification;
    private Boolean gonggaoNotification;

}