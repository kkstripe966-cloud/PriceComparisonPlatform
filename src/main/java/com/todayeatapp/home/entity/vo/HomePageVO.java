package com.todayeatapp.entity.vo;

import com.todayeatapp.entity.Category;
import com.todayeatapp.entity.Dish;
import lombok.Data;
import java.util.List;

@Data
public class HomePageVO {
    private List<Category> categories;      // 分类列表
    private List<Dish> hotDishes;           // 热门菜品
    private List<Dish> recommendDishes;     // 推荐菜品
    private Integer currentCategoryId;      // 当前分类ID
    private String searchKeyword;           // 搜索关键词
    private Integer totalDishes;            // 菜品总数

    // 构造方法
    public HomePageVO() {}

    public HomePageVO(List<Category> categories, List<Dish> hotDishes) {
        this.categories = categories;
        this.hotDishes = hotDishes;
    }
}