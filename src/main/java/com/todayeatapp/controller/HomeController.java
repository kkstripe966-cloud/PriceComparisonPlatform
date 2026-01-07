package com.todayeatapp.controller;

import com.todayeatapp.entity.Category;
import com.todayeatapp.entity.Dish;
import com.todayeatapp.entity.vo.HomePageVO;
import com.todayeatapp.service.HomeService;
import com.todayeatapp.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取首页数据
     */
    @GetMapping("/index")
    public ApiResponse<HomePageVO> getHomePage() {
        try {
            HomePageVO homePageData = homeService.getHomePageData();
            return ApiResponse.success("获取首页数据成功", homePageData);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取首页数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        try {
            List<Category> categories = homeService.getCategories();
            return ApiResponse.success("获取分类成功", categories);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取分类失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类获取菜品
     */
    @GetMapping("/dishes/category/{categoryId}")
    public ApiResponse<Map<String, Object>> getDishesByCategory(
            @PathVariable(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize) {

        try {
            List<Dish> dishes = homeService.getDishesByCategory(categoryId, page, pageSize);
            Map<String, Object> result = new HashMap<>();
            result.put("list", dishes);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("total", dishes.size());

            return ApiResponse.success("获取菜品成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取菜品失败: " + e.getMessage());
        }
    }

    /**
     * 搜索菜品
     */
    @GetMapping("/dishes/search")
    public ApiResponse<Map<String, Object>> searchDishes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize) {

        try {
            List<Dish> dishes = homeService.searchDishes(keyword, page, pageSize);
            Map<String, Object> result = new HashMap<>();
            result.put("list", dishes);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("keyword", keyword);
            result.put("total", dishes.size());

            return ApiResponse.success("搜索成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜品详情
     */
    @GetMapping("/dish/{dishId}")
    public ApiResponse<Dish> getDishDetail(@PathVariable Integer dishId) {
        try {
            Dish dish = homeService.getDishDetail(dishId);
            return ApiResponse.success("获取菜品详情成功", dish);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取菜品详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门菜品
     */
    @GetMapping("/dishes/hot")
    public ApiResponse<List<Dish>> getHotDishes(
            @RequestParam(defaultValue = "8") Integer limit) {

        try {
            List<Dish> dishes = homeService.getHotDishes(limit);
            return ApiResponse.success("获取热门菜品成功", dishes);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取热门菜品失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐菜品
     */
    @GetMapping("/dishes/recommend")
    public ApiResponse<List<Dish>> getRecommendDishes(
            @RequestParam(defaultValue = "6") Integer limit) {

        try {
            List<Dish> dishes = homeService.getRecommendDishes(limit);
            return ApiResponse.success("获取推荐菜品成功", dishes);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取推荐菜品失败: " + e.getMessage());
        }
    }

    /**
     * 记录比价点击
     */
    @PostMapping("/record/compare")
    public ApiResponse<Void> recordCompare(@RequestParam Integer dishId) {
        try {
            homeService.recordCompare(dishId);
            return ApiResponse.success("记录成功", null);
        } catch (Exception e) {
            return ApiResponse.error(500, "记录失败: " + e.getMessage());
        }
    }
}