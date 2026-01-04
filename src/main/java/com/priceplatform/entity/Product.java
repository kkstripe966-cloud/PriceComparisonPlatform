package com.priceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Product {
    private Long id;
    private String name;                // 商品名称
    private String description;         // 商品描述
    private String imageUrl;           // 商品图片URL
    private String category;           // 商品分类
    private String tags;               // 标签（逗号分隔）
    private Integer popularity;        // 人气指数
    private Integer status;            // 状态：1-正常，0-下架
    private Date createTime;
    private Date updateTime;

    // 非数据库字段
    private List<PlatformPrice> platformPrices;  // 各平台价格
    private BigDecimal minPrice;        // 最低价格
    private String minPricePlatform;    // 最低价平台
}