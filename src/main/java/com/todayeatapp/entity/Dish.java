package com.todayeatapp.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Dish {
    private Integer dishId;          // 菜品ID
    private String dishName;         // 菜品名称
    private String description;      // 描述
    private String mainImage;        // 主图URL
    private BigDecimal minPrice;     // 最低价格
    private BigDecimal maxPrice;     // 最高价格
    private BigDecimal avgPrice;     // 平均价格
    private Integer categoryId;      // 分类ID
    private String tags;            // 标签(逗号分隔)
    private Boolean isHot;           // 是否热门
    private Boolean isRecommend;     // 是否推荐
    private Integer viewCount;       // 浏览次数
    private Integer compareCount;    // 比价次数
    private Integer status;          // 状态:1-正常,0-下架
    private Date createTime;         // 创建时间
    private Date updateTime;         // 更新时间

    // 非数据库字段
    private String categoryName;     // 分类名称
    private List<String> tagList;    // 标签列表

    // 构造函数
    public Dish() {}

    public Dish(String dishName, BigDecimal minPrice, String mainImage) {
        this.dishName = dishName;
        this.minPrice = minPrice;
        this.mainImage = mainImage;
    }
}