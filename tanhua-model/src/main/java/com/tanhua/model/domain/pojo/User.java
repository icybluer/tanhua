package com.tanhua.model.domain.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User extends BasePojo implements Serializable {
    private Long id;
    private String mobile;
    private String password;
    private String hxUser;
    private String hxPassword;
}
