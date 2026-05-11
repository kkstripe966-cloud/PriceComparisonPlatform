package com.todayeatapp.auth.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String phone;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private Integer status;
    private Date lastLoginTime;
    private Date createTime;
    private Date updateTime;
}