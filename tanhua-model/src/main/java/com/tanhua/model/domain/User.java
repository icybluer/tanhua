package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User extends BasePojo implements Serializable {
    private Long id;
    private String mobile;
    private String password;
    private String hxUser;
    private String hxPassword;
}
