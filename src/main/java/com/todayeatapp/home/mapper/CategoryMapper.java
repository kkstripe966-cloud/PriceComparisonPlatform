package com.todayeatapp.mapper;

import com.todayeatapp.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 获取所有可用分类
     */
    @Select("SELECT category_id, category_name, category_icon, sort_order, is_active, create_time " +
            "FROM category WHERE is_active = true ORDER BY sort_order ASC")
    List<Category> selectAllCategories();

    /**
     * 根据ID查询分类
     */
    @Select("SELECT category_id, category_name, category_icon, sort_order, is_active, create_time " +
            "FROM category WHERE category_id = #{categoryId} AND is_active = true")
    Category selectCategoryById(@Param("categoryId") Integer categoryId);

    /**
     * 查询热门分类
     */
    @Select("SELECT c.* FROM category c " +
            "JOIN dish d ON c.category_id = d.category_id " +
            "WHERE c.is_active = true AND d.status = 1 " +
            "GROUP BY c.category_id " +
            "ORDER BY COUNT(d.dish_id) DESC " +
            "LIMIT #{limit}")
    List<Category> selectHotCategories(@Param("limit") Integer limit);
}