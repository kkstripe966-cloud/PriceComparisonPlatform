package com.todayeatapp.mapper;

import com.todayeatapp.entity.Dish;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 获取热门菜品
     */
    @Select("SELECT d.dish_id, d.dish_name, d.main_image, d.min_price, d.avg_price, " +
            "d.category_id, d.tags, d.is_hot, d.is_recommend, d.view_count, " +
            "c.category_name " +
            "FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.category_id " +
            "WHERE d.status = 1 AND d.is_hot = true " +
            "ORDER BY d.view_count DESC, d.compare_count DESC " +
            "LIMIT #{limit}")
    List<Dish> selectHotDishes(@Param("limit") Integer limit);

    /**
     * 获取推荐菜品
     */
    @Select("SELECT d.dish_id, d.dish_name, d.main_image, d.min_price, d.avg_price, " +
            "d.category_id, d.tags, d.is_hot, d.is_recommend, d.view_count, " +
            "c.category_name " +
            "FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.category_id " +
            "WHERE d.status = 1 AND d.is_recommend = true " +
            "ORDER BY d.create_time DESC " +
            "LIMIT #{limit}")
    List<Dish> selectRecommendDishes(@Param("limit") Integer limit);

    /**
     * 根据分类获取菜品
     */
    @Select("<script>" +
            "SELECT d.dish_id, d.dish_name, d.main_image, d.min_price, d.avg_price, " +
            "d.category_id, d.tags, d.is_hot, d.is_recommend, d.view_count, " +
            "c.category_name " +
            "FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.category_id " +
            "WHERE d.status = 1 " +
            "<if test='categoryId != null'>" +
            "AND d.category_id = #{categoryId} " +
            "</if>" +
            "ORDER BY d.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Dish> selectDishesByCategory(@Param("categoryId") Integer categoryId,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    /**
     * 搜索菜品
     */
    @Select("<script>" +
            "SELECT d.dish_id, d.dish_name, d.main_image, d.min_price, d.avg_price, " +
            "d.category_id, d.tags, d.is_hot, d.is_recommend, d.view_count, " +
            "c.category_name " +
            "FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.category_id " +
            "WHERE d.status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (d.dish_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR d.tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY d.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Dish> searchDishes(@Param("keyword") String keyword,
                            @Param("offset") Integer offset,
                            @Param("pageSize") Integer pageSize);

    /**
     * 根据ID查询菜品详情
     */
    @Select("SELECT d.*, c.category_name " +
            "FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.category_id " +
            "WHERE d.dish_id = #{dishId} AND d.status = 1")
    Dish selectDishById(@Param("dishId") Integer dishId);

    /**
     * 统计菜品总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM dish WHERE status = 1 " +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId}" +
            "</if>" +
            "</script>")
    int countDishes(@Param("categoryId") Integer categoryId);

    /**
     * 统计搜索结果数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM dish " +
            "WHERE status = 1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (dish_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR tags LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    int countSearchResults(@Param("keyword") String keyword);

    /**
     * 更新浏览次数
     */
    @Update("UPDATE dish SET view_count = view_count + 1 WHERE dish_id = #{dishId}")
    int incrementViewCount(@Param("dishId") Integer dishId);

    /**
     * 更新比价次数
     */
    @Update("UPDATE dish SET compare_count = compare_count + 1 WHERE dish_id = #{dishId}")
    int incrementCompareCount(@Param("dishId") Integer dishId);
}