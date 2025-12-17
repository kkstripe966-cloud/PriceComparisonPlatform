package com.priceplatform.controller;

import com.priceplatform.service.UserService;
import com.priceplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param account 手机号或邮箱
     * @param type 类型：1-注册，2-登录，3-重置密码
     * @return 响应结果
     */
    @PostMapping("/sendCode")
    public Map<String, Object> sendVerificationCode(
            @RequestParam String account,
            @RequestParam Integer type) {

        if (type < 1 || type > 3) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "验证码类型错误");
            return result;
        }

        return userService.sendVerificationCode(account, type);
    }

    /**
     * 注册接口
     * @param phone 手机号
     * @param email 邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param code 验证码
     * @return 注册结果
     */
    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String code) {

        return userService.register(phone, email, password, confirmPassword, code);
    }

    /**
     * 登录接口
     * @param username 手机号或邮箱
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String username,
            @RequestParam String password) {

        return userService.login(username, password);
    }

    /**
     * 获取用户信息
     * @param request HttpServletRequest
     * @return 用户信息
     */
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "未授权访问");
            return result;
        }

        String userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "token无效");
            return result;
        }

        return userService.getUserInfo(Long.parseLong(userId));
    }

    /**
     * 检查手机号是否已注册
     * @param phone 手机号
     * @return 检查结果
     */
    @GetMapping("/checkPhone")
    public Map<String, Object> checkPhone(@RequestParam String phone) {
        boolean exists = userService.isPhoneRegistered(phone);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", exists);
        result.put("message", exists ? "手机号已注册" : "手机号可用");
        return result;
    }

    /**
     * 检查邮箱是否已注册
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/checkEmail")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailRegistered(email);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", exists);
        result.put("message", exists ? "邮箱已注册" : "邮箱可用");
        return result;
    }

    /**
     * 测试接口
     * @return 测试结果
     */
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "后端服务正常运行");
        return result;
    }
}