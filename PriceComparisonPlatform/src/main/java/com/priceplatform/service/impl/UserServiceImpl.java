package com.priceplatform.service.impl;

import com.priceplatform.dao.UserMapper;
import com.priceplatform.dao.VerificationCodeMapper;
import com.priceplatform.entity.User;
import com.priceplatform.entity.VerificationCode;
import com.priceplatform.service.UserService;
import com.priceplatform.util.JwtUtil;
import com.priceplatform.util.ValidateUtil;
import com.priceplatform.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;
    // 验证码发送间隔（秒）
    private static final int CODE_SEND_INTERVAL = 60;

    @Override
    public Map<String, Object> sendVerificationCode(String account, Integer type) {
        Map<String, Object> result = new HashMap<>();

        // 验证账号格式
        if (!ValidateUtil.isValidPhone(account) && !ValidateUtil.isValidEmail(account)) {
            result.put("success", false);
            result.put("message", "请输入正确的手机号或邮箱");
            return result;
        }

        // 检查是否已注册
        if (type == 1) { // 注册类型
            if (ValidateUtil.isValidPhone(account) && isPhoneRegistered(account)) {
                result.put("success", false);
                result.put("message", "手机号已注册");
                return result;
            }
            if (ValidateUtil.isValidEmail(account) && isEmailRegistered(account)) {
                result.put("success", false);
                result.put("message", "邮箱已注册");
                return result;
            }
        }

        // 检查发送间隔
        VerificationCode latestCode = verificationCodeMapper.selectLatestByAccountAndType(account, type);
        if (latestCode != null) {
            long diff = System.currentTimeMillis() - latestCode.getCreateTime().getTime();
            if (diff < CODE_SEND_INTERVAL * 1000) {
                result.put("success", false);
                result.put("message", "验证码发送过于频繁，请稍后再试");
                return result;
            }
        }

        // 生成验证码
        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        // 创建验证码记录
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setAccount(account);
        verificationCode.setCode(code);
        verificationCode.setType(type);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, CODE_EXPIRE_MINUTES);
        verificationCode.setExpireTime(calendar.getTime());

        verificationCode.setIsUsed(0);

        verificationCodeMapper.insert(verificationCode);

        if (ValidateUtil.isValidEmail(account)) {
            try {
                emailService.sendVerificationCode(account, code);
                result.put("success", true);
                result.put("message", "验证码已发送到您的邮箱");
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "邮件发送失败：" + e.getMessage());
            }
        }else {
            // 手机号不发送验证码
            result.put("success", false);
            result.put("message", "暂不支持手机验证码，请输入邮箱");
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> register(String phone, String email, String password,
                                        String confirmPassword, String code) {
        Map<String, Object> result = new HashMap<>();

        // 基本验证
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(email) ||
                StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword) ||
                StringUtils.isEmpty(code)) {
            result.put("success", false);
            result.put("message", "请填写完整信息");
            return result;
        }

        if (!ValidateUtil.isValidPhone(phone)) {
            result.put("success", false);
            result.put("message", "手机号格式不正确");
            return result;
        }

        if (!ValidateUtil.isValidEmail(email)) {
            result.put("success", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }

        if (!password.equals(confirmPassword)) {
            result.put("success", false);
            result.put("message", "两次输入的密码不一致");
            return result;
        }

        if (password.length() < 6 || password.length() > 20) {
            result.put("success", false);
            result.put("message", "密码长度需在6-20位之间");
            return result;
        }

        // 检查是否已注册
        if (isPhoneRegistered(phone)) {
            result.put("success", false);
            result.put("message", "手机号已注册");
            return result;
        }

        if (isEmailRegistered(email)) {
            result.put("success", false);
            result.put("message", "邮箱已注册");
            return result;
        }

        // 验证验证码
        VerificationCode verificationCode = verificationCodeMapper.selectLatestByAccountAndType(email, 1);
        if (verificationCode == null) {
            verificationCode = verificationCodeMapper.selectLatestByAccountAndType(phone, 1);
        }

        if (verificationCode == null) {
            result.put("success", false);
            result.put("message", "请先获取验证码");
            return result;
        }

        if (verificationCode.getIsUsed() == 1) {
            result.put("success", false);
            result.put("message", "验证码已被使用");
            return result;
        }

        if (verificationCode.getExpireTime().before(new Date())) {
            result.put("success", false);
            result.put("message", "验证码已过期");
            return result;
        }

        if (!verificationCode.getCode().equals(code)) {
            result.put("success", false);
            result.put("message", "验证码错误");
            return result;
        }

        // 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setAvatar("");
        user.setStatus(1);

        int insertResult = userMapper.insert(user);
        if (insertResult > 0) {
            // 标记验证码为已使用
            verificationCodeMapper.updateIsUsed(verificationCode.getId(), 1);

            // 生成token
            String token = JwtUtil.createToken(user.getId().toString(), user.getPhone());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("avatar", user.getAvatar());

            result.put("success", true);
            result.put("message", "注册成功");
            result.put("token", token);
            result.put("userInfo", userInfo);
        } else {
            result.put("success", false);
            result.put("message", "注册失败，请稍后重试");
        }

        return result;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            result.put("success", false);
            result.put("message", "用户名和密码不能为空");
            return result;
        }

        // 查询用户
        User user = userMapper.selectByPhoneOrEmail(username);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        // 调试日志
        System.out.println("登录用户信息：");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Phone: " + user.getPhone());
        System.out.println("  Status: " + user.getStatus());
        System.out.println("  Status class: " + (user.getStatus() != null ? user.getStatus().getClass().getName() : "null"));

        // 检查
        if (user.getStatus() == null) {
            result.put("success", false);
            result.put("message", "账号状态异常");
            return result;
        }

        if (user.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId(), new Date());

        // 生成token
        String token = JwtUtil.createToken(user.getId().toString(), user.getPhone());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("userInfo", userInfo);

        return result;
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        Map<String, Object> result = new HashMap<>();

        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (user.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("lastLoginTime", user.getLastLoginTime());

        result.put("success", true);
        result.put("message", "获取成功");
        result.put("data", userInfo);

        return result;
    }

    @Override
    public boolean isPhoneRegistered(String phone) {
        return userMapper.existsByPhone(phone);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userMapper.existsByEmail(email);
    }
}