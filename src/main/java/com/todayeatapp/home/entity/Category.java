package com.todayeatapp.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Category {
    private Integer categoryId;      // 分类ID
    private String categoryName;     // 分类名称
    private String categoryIcon;     // 分类图标
    private Integer sortOrder;       // 排序
    private Boolean isActive;        // 是否启用
    private Date createTime;         // 创建时间

    // 构造函数
    public Category() {}

    public Category(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}