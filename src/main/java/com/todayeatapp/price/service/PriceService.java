package com.priceplatform.service;

import com.priceplatform.dto.PriceCompareDTO;
import com.priceplatform.dto.PriceResponse;
import com.priceplatform.entity.PlatformPrice;
import com.priceplatform.entity.Product;
import com.priceplatform.entity.PriceHistory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface PriceService {

    // 获取商品详情
    Product getProductById(Long productId);

    // 获取商品比价详情
    PriceCompareDTO getPriceDetail(Long productId);

    // 刷新价格
    PriceCompareDTO refreshPrice(Long productId);

    // 获取相似商品推荐
    PriceCompareDTO.RecommendProductDTO getRecommendations(Long productId);

    // 获取价格趋势
    Map<String, Object> getPriceTrend(Long productId, String platform, Integer days);

    // 添加/更新价格
    int saveOrUpdatePrice(PlatformPrice price);

    // 记录价格历史
    int recordPriceHistory(PriceHistory history);

    // 价格预警分析
    void analyzePriceWarning(PlatformPrice price);

    // 获取热门商品
    List<Product> getHotProducts(Integer limit);

    // 搜索商品
    List<Product> searchProducts(String keyword);

    // 计算最优购买方案
    Map<String, Object> calculateBestPlan(Long productId);
}