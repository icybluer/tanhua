package com.tanhua.model.domain.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private Long id;
    private String gender;
    private String nickname;
    private String birthday;
    private String city;
    private String avatar;
    private String education;
    private String income;
    private String profession;
    private Integer marriage;
}
