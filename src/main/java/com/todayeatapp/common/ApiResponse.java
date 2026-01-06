package com.todayeatapp.common;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;      // 响应码
    private String message;    // 响应消息
    private T data;           // 响应数据
    private Long timestamp;    // 时间戳

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}