package com.todayeatapp.service;

import com.todayeatapp.entity.Category;
import com.todayeatapp.entity.Dish;
import com.todayeatapp.entity.vo.HomePageVO;
import java.util.List;

public interface HomeService {

    /**
     * 获取首页数据
     */
    HomePageVO getHomePageData();

    /**
     * 获取分类列表
     */
    List<Category> getCategories();

    /**
     * 根据分类获取菜品
     */
    List<Dish> getDishesByCategory(Integer categoryId, Integer page, Integer pageSize);

    /**
     * 搜索菜品
     */
    List<Dish> searchDishes(String keyword, Integer page, Integer pageSize);

    /**
     * 获取菜品详情
     */
    Dish getDishDetail(Integer dishId);

    /**
     * 获取热门菜品
     */
    List<Dish> getHotDishes(Integer limit);

    /**
     * 获取推荐菜品
     */
    List<Dish> getRecommendDishes(Integer limit);

    /**
     * 记录菜品查看
     */
    void recordView(Integer dishId);

    /**
     * 记录比价点击
     */
    void recordCompare(Integer dishId);
}