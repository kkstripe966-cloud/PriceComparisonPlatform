package com.todayeatapp.price.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlatformPrice {
    private Long id;
    private Long productId;             // 商品ID
    private String platform;            // 平台标识：meituan/eleme/jd
    private String platformName;        // 平台显示名称
    private BigDecimal originalPrice;   // 原价
    private BigDecimal currentPrice;    // 现价
    private BigDecimal discount;        // 折扣
    private String couponInfo;          // 优惠信息
    private BigDecimal deliveryFee;     // 配送费
    private BigDecimal minOrder;        // 起送价
    private BigDecimal rating;          // 平台评分
    private Integer salesMonth;         // 月销量
    private Integer warningLevel;       // 预警级别：0-正常，1-警告，2-严重
    private String warningReason;       // 预警原因
    private Date updateTime;

    // 非数据库字段
    private BigDecimal finalPrice;      // 最终支付价 = 现价 + 配送费
    private BigDecimal saveAmount;      // 节省金额
    private BigDecimal savePercent;     // 节省百分比
    private Boolean isBestChoice;       // 是否是最佳选择
}