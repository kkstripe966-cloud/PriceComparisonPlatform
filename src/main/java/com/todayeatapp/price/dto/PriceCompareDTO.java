package com.todayeatapp.price.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PriceCompareDTO {
    // 商品信息
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private String category;
    private String tags;
    private Integer popularity;

    // 价格信息
    private List<PlatformPriceDTO> priceList;
    private BigDecimal lowestPrice;          // 最低价格
    private String lowestPricePlatform;     // 最低价平台
    private BigDecimal averagePrice;        // 平均价格
    private BigDecimal saveAmount;          // 可节省金额
    private BigDecimal savePercent;         // 可节省百分比

    // 预警信息
    private Boolean hasWarning;             // 是否有预警
    private String warningMessage;          // 预警信息
    private Integer maxWarningLevel;        // 最高预警级别

    // 最佳选择
    private PlatformPriceDTO bestChoice;

    // 智能推荐
    private List<RecommendProductDTO> recommendations;

    // 优惠建议
    private List<String> suggestions;

    // 统计信息
    private Map<String, Object> statistics;

    @Data
    public static class PlatformPriceDTO {
        private Long id;
        private String platform;            // 平台标识
        private String platformName;        // 平台显示名称
        private BigDecimal originalPrice;   // 原价
        private BigDecimal currentPrice;    // 现价
        private BigDecimal deliveryFee;     // 配送费
        private BigDecimal finalPrice;      // 最终支付价
        private BigDecimal discount;        // 折扣
        private String couponInfo;          // 优惠信息
        private BigDecimal minOrder;        // 起送价
        private BigDecimal rating;          // 评分
        private Integer salesMonth;         // 月销量
        private BigDecimal saveAmount;      // 节省金额
        private BigDecimal savePercent;     // 节省百分比
        private Boolean isBestChoice;       // 是否最佳选择
        private Integer warningLevel;       // 预警级别
        private String warningReason;       // 预警原因
        private String buyUrl;              // 购买链接
        private String updateTime;          // 更新时间
    }

    @Data
    public static class RecommendProductDTO {
        private Long id;
        private String name;
        private String description;
        private String image;
        private BigDecimal price;
        private String platform;
        private String reason;              // 推荐理由
        private BigDecimal priceAdvantage;  // 价格优势
    }
}