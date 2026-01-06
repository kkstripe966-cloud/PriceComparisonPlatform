package com.todayeatapp.service.impl;

import com.todayeatapp.entity.Category;
import com.todayeatapp.entity.Dish;
import com.todayeatapp.entity.vo.HomePageVO;
import com.todayeatapp.mapper.CategoryMapper;
import com.todayeatapp.mapper.DishMapper;
import com.todayeatapp.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HomeServiceImpl implements HomeService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Value("${home.page.size:12}")
    private int defaultPageSize;

    @Value("${home.hot.limit:8}")
    private int hotDishesLimit;

    @Value("${home.recommend.limit:6}")
    private int recommendDishesLimit;

    @Override
    public HomePageVO getHomePageData() {
        HomePageVO homePageVO = new HomePageVO();

        // 获取分类
        List<Category> categories = getCategories();
        homePageVO.setCategories(categories);

        // 获取热门菜品
        List<Dish> hotDishes = getHotDishes(hotDishesLimit);
        processTags(hotDishes);
        homePageVO.setHotDishes(hotDishes);

        // 获取推荐菜品
        List<Dish> recommendDishes = getRecommendDishes(recommendDishesLimit);
        processTags(recommendDishes);
        homePageVO.setRecommendDishes(recommendDishes);

        // 统计总数
        int totalDishes = dishMapper.countDishes(null);
        homePageVO.setTotalDishes(totalDishes);

        return homePageVO;
    }

    @Override
    public List<Category> getCategories() {
        return categoryMapper.selectAllCategories();
    }

    @Override
    public List<Dish> getDishesByCategory(Integer categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = defaultPageSize;

        int offset = (page - 1) * pageSize;
        List<Dish> dishes = dishMapper.selectDishesByCategory(categoryId, offset, pageSize);
        processTags(dishes);

        return dishes;
    }

    @Override
    public List<Dish> searchDishes(String keyword, Integer page, Integer pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return getDishesByCategory(null, page, pageSize);
        }

        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = defaultPageSize;

        int offset = (page - 1) * pageSize;
        List<Dish> dishes = dishMapper.searchDishes(keyword, offset, pageSize);
        processTags(dishes);

        return dishes;
    }

    @Override
    public Dish getDishDetail(Integer dishId) {
        if (dishId == null) {
            throw new IllegalArgumentException("菜品ID不能为空");
        }

        Dish dish = dishMapper.selectDishById(dishId);
        if (dish == null) {
            throw new RuntimeException("菜品不存在或已下架");
        }

        // 处理标签
        if (StringUtils.hasText(dish.getTags())) {
            List<String> tagList = Arrays.stream(dish.getTags().split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            dish.setTagList(tagList);
        }

        // 记录查看
        recordView(dishId);

        return dish;
    }

    @Override
    public List<Dish> getHotDishes(Integer limit) {
        if (limit == null || limit < 1) limit = hotDishesLimit;
        return dishMapper.selectHotDishes(limit);
    }

    @Override
    public List<Dish> getRecommendDishes(Integer limit) {
        if (limit == null || limit < 1) limit = recommendDishesLimit;
        return dishMapper.selectRecommendDishes(limit);
    }

    @Override
    public void recordView(Integer dishId) {
        if (dishId != null) {
            dishMapper.incrementViewCount(dishId);
        }
    }

    @Override
    public void recordCompare(Integer dishId) {
        if (dishId != null) {
            dishMapper.incrementCompareCount(dishId);
        }
    }

    /**
     * 处理菜品标签
     */
    private void processTags(List<Dish> dishes) {
        if (dishes == null) return;

        for (Dish dish : dishes) {
            if (StringUtils.hasText(dish.getTags())) {
                List<String> tagList = Arrays.stream(dish.getTags().split(","))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
                dish.setTagList(tagList);
            }
        }
    }
}