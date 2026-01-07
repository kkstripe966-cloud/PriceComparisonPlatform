package com.priceplatform.dto;

import lombok.Data;
import java.util.List;

@Data
public class PriceResponse<T> {
    private Integer code;        // 状态码：200-成功，400-失败
    private String message;      // 响应消息
    private T data;              // 响应数据
    private Long timestamp;      // 时间戳

    public PriceResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public PriceResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> PriceResponse<T> success(T data) {
        PriceResponse<T> response = new PriceResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> PriceResponse<T> success(String message, T data) {
        PriceResponse<T> response = new PriceResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> PriceResponse<T> error(String message) {
        PriceResponse<T> response = new PriceResponse<>();
        response.setCode(400);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> PriceResponse<T> error(Integer code, String message) {
        PriceResponse<T> response = new PriceResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}