package com.todayeatapp.auth.entity;

import lombok.Data;
import java.util.Date;

@Data
public class VerificationCode {
    private Long id;
    private String account;  // 手机号或邮箱
    private String code;     // 验证码
    private Integer type;    // 1-注册，2-登录，3-重置密码
    private Date expireTime; // 过期时间
    private Integer isUsed;  // 是否已使用：0-未使用，1-已使用
    private Date createTime; //创建时间
}