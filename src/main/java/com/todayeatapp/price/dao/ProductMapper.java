package com.priceplatform.dao;

import com.priceplatform.entity.Product;
import com.priceplatform.entity.PlatformPrice;
import com.priceplatform.entity.PriceHistory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ProductMapper {

    // 商品相关操作
    @Select("SELECT * FROM product WHERE id = #{id} AND status = 1")
    Product getProductById(@Param("id") Long id);

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY popularity DESC LIMIT #{limit}")
    List<Product> getHotProducts(@Param("limit") Integer limit);

    @Select("SELECT * FROM product WHERE category = #{category} AND status = 1")
    List<Product> getProductsByCategory(@Param("category") String category);

    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%') AND status = 1")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // 平台价格相关操作
    @Select("SELECT * FROM platform_price WHERE product_id = #{productId} ORDER BY current_price ASC")
    List<PlatformPrice> getPricesByProductId(@Param("productId") Long productId);

    @Select("SELECT * FROM platform_price WHERE id = #{id}")
    PlatformPrice getPriceById(@Param("id") Long id);

    @Select("SELECT * FROM platform_price WHERE product_id = #{productId} AND platform = #{platform}")
    PlatformPrice getPriceByProductAndPlatform(@Param("productId") Long productId,
                                               @Param("platform") String platform);

    @Select("SELECT * FROM platform_price WHERE product_id = #{productId} ORDER BY update_time DESC LIMIT 1")
    PlatformPrice getLatestPrice(@Param("productId") Long productId);

    @Insert("INSERT INTO platform_price (product_id, platform, platform_name, original_price, current_price, " +
            "discount, coupon_info, delivery_fee, min_order, rating, sales_month, warning_level, warning_reason) " +
            "VALUES (#{productId}, #{platform}, #{platformName}, #{originalPrice}, #{currentPrice}, " +
            "#{discount}, #{couponInfo}, #{deliveryFee}, #{minOrder}, #{rating}, #{salesMonth}, " +
            "#{warningLevel}, #{warningReason})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPrice(PlatformPrice price);

    @Update("UPDATE platform_price SET " +
            "original_price = #{originalPrice}, " +
            "current_price = #{currentPrice}, " +
            "discount = #{discount}, " +
            "coupon_info = #{couponInfo}, " +
            "delivery_fee = #{deliveryFee}, " +
            "min_order = #{minOrder}, " +
            "rating = #{rating}, " +
            "sales_month = #{salesMonth}, " +
            "warning_level = #{warningLevel}, " +
            "warning_reason = #{warningReason} " +
            "WHERE id = #{id}")
    int updatePrice(PlatformPrice price);

    // 统计查询
    @Select("SELECT " +
            "MIN(current_price) as minPrice, " +
            "MAX(current_price) as maxPrice, " +
            "AVG(current_price) as avgPrice, " +
            "COUNT(*) as platformCount " +
            "FROM platform_price WHERE product_id = #{productId}")
    Map<String, Object> getPriceStatistics(@Param("productId") Long productId);

    // 获取相似商品
    @Select("SELECT p.*, MIN(pp.current_price) as minPrice " +
            "FROM product p " +
            "LEFT JOIN platform_price pp ON p.id = pp.product_id " +
            "WHERE p.category = (SELECT category FROM product WHERE id = #{productId}) " +
            "AND p.id != #{productId} " +
            "AND p.status = 1 " +
            "GROUP BY p.id " +
            "ORDER BY p.popularity DESC " +
            "LIMIT #{limit}")
    List<Product> getSimilarProducts(@Param("productId") Long productId,
                                     @Param("limit") Integer limit);

    // 价格历史相关操作
    @Insert("INSERT INTO price_history (platform_price_id, price, change_type, change_percent) " +
            "VALUES (#{platformPriceId}, #{price}, #{changeType}, #{changePercent})")
    int insertPriceHistory(PriceHistory history);

    @Select("SELECT * FROM price_history " +
            "WHERE platform_price_id = #{platformPriceId} " +
            "ORDER BY record_time DESC " +
            "LIMIT #{limit}")
    List<PriceHistory> getPriceHistory(@Param("platformPriceId") Long platformPriceId,
                                       @Param("limit") Integer limit);

    Map<String, Object> getPriceTrend(Map<String, Object> params);
}