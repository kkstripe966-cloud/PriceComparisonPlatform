package com.priceplatform.service;

import java.util.Map;

public interface UserService {
    // 发送验证码
    Map<String, Object> sendVerificationCode(String account, Integer type);

    // 注册
    Map<String, Object> register(String phone, String email, String password,
                                 String confirmPassword, String code);

    // 登录
    Map<String, Object> login(String username, String password);

    // 获取用户信息
    Map<String, Object> getUserInfo(Long userId);

    // 检查手机号是否已注册
    boolean isPhoneRegistered(String phone);

    // 检查邮箱是否已注册
    boolean isEmailRegistered(String email);
}