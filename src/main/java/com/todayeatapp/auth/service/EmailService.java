package com.todayeatapp.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 发件人邮箱（需要和配置中的一致）
    private final String fromEmail = "2313751423@qq.com";

    /**
     * 发送验证码邮件
     */
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("今天吃什么Pro - 验证码");

            // HTML格式的邮件内容
            String content = buildEmailContent(code);
            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建邮件内容
     */
    private String buildEmailContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px; }" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "        .header { text-align: center; margin-bottom: 30px; }" +
                "        .header h1 { color: #667eea; margin: 0; }" +
                "        .code-box { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; font-size: 32px; font-weight: bold; text-align: center; padding: 20px; border-radius: 8px; letter-spacing: 5px; margin: 20px 0; }" +
                "        .footer { margin-top: 30px; text-align: center; color: #666; font-size: 12px; }" +
                "        .tip { color: #ff6b6b; font-weight: bold; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>今天吃什么Pro</h1>" +
                "        </div>" +
                "        <p>尊敬的用户，您好！</p>" +
                "        <p>您正在注册今天吃什么Pro平台账户，验证码为：</p>" +
                "        <div class=\"code-box\">" + code + "</div>" +
                "        <p>请在5分钟内完成验证，验证码仅用于注册，请勿泄露给他人。</p>" +
                "        <p class=\"tip\">如果不是您本人操作，请忽略此邮件。</p>" +
                "        <div class=\"footer\">" +
                "            <p>© 2025 今天吃什么Pro 版权所有</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}