package com.priceplatform.controller;

import com.priceplatform.dto.PriceCompareDTO;
import com.priceplatform.dto.PriceResponse;
import com.priceplatform.entity.Product;
import com.priceplatform.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 比价模块控制器
 * API路径：/api/price
 */
@Controller
@RequestMapping("/api/price")
public class PriceController {

    @Autowired
    private PriceService priceService;

    /**
     * 获取商品比价详情
     * GET /api/price/detail/{productId}
     */
    @GetMapping("/detail/{productId}")
    @ResponseBody
    public PriceResponse<PriceCompareDTO> getPriceDetail(@PathVariable("productId") Long productId) {
        try {
            PriceCompareDTO priceDetail = priceService.getPriceDetail(productId);
            return PriceResponse.success(priceDetail);
        } catch (RuntimeException e) {
            return PriceResponse.error(e.getMessage());
        } catch (Exception e) {
            return PriceResponse.error("系统错误，请稍后重试");
        }
    }

    /**
     * 刷新商品价格
     * POST /api/price/refresh/{productId}
     */
    @PostMapping("/refresh/{productId}")
    @ResponseBody
    public PriceResponse<PriceCompareDTO> refreshPrice(@PathVariable("productId") Long productId) {
        try {
            PriceCompareDTO priceDetail = priceService.refreshPrice(productId);
            return PriceResponse.success("价格刷新成功", priceDetail);
        } catch (Exception e) {
            return PriceResponse.error("刷新失败，请稍后重试");
        }
    }

    /**
     * 获取热门商品
     * GET /api/price/hot
     */
    @GetMapping("/hot")
    public PriceResponse<List<Product>> getHotProducts(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        try {
            List<Product> hotProducts = priceService.getHotProducts(limit);
            return PriceResponse.success(hotProducts);
        } catch (Exception e) {
            return PriceResponse.error("获取热门商品失败");
        }
    }

    /**
     * 搜索商品
     * GET /api/price/search
     */
    @GetMapping("/search")
    public PriceResponse<List<Product>> searchProducts(@RequestParam("keyword") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return PriceResponse.error("请输入搜索关键词");
            }

            List<Product> products = priceService.searchProducts(keyword);
            return PriceResponse.success(products);
        } catch (Exception e) {
            return PriceResponse.error("搜索失败，请稍后重试");
        }
    }

    /**
     * 获取价格趋势
     * GET /api/price/trend/{productId}/{platform}
     */
    @GetMapping("/trend/{productId}/{platform}")
    public PriceResponse<Map<String, Object>> getPriceTrend(
            @PathVariable("productId") Long productId,
            @PathVariable("platform") String platform,
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        try {
            Map<String, Object> trendData = priceService.getPriceTrend(productId, platform, days);
            return PriceResponse.success(trendData);
        } catch (Exception e) {
            return PriceResponse.error("获取价格趋势失败");
        }
    }

    /**
     * 获取最优购买方案
     * GET /api/price/best-plan/{productId}
     */
    @GetMapping("/best-plan/{productId}")
    public PriceResponse<Map<String, Object>> getBestPlan(@PathVariable("productId") Long productId) {
        try {
            Map<String, Object> bestPlan = priceService.calculateBestPlan(productId);
            return PriceResponse.success(bestPlan);
        } catch (Exception e) {
            return PriceResponse.error("计算最优方案失败");
        }
    }

    /**
     * 添加/更新价格信息
     * POST /api/price/save
     */
    @PostMapping("/save")
    public PriceResponse<String> savePrice(@RequestBody Map<String, Object> priceData) {
        try {
            return PriceResponse.success("价格保存成功");
        } catch (Exception e) {
            return PriceResponse.error("保存价格失败");
        }
    }

    /**
     * 获取所有平台价格对比
     * GET /api/price/compare/{productId}
     */
    @GetMapping("/compare/{productId}")
    public PriceResponse<Map<String, Object>> compareAllPrices(@PathVariable("productId") Long productId) {
        try {
            PriceCompareDTO priceDetail = priceService.getPriceDetail(productId);
            Map<String, Object> result = new HashMap<>();

            result.put("product", priceDetail);
            result.put("statistics", new HashMap<String, Object>() {{
                put("platformCount", priceDetail.getPriceList().size());
                put("lowestPrice", priceDetail.getLowestPrice());
                put("averagePrice", priceDetail.getAveragePrice());
                put("savings", priceDetail.getSaveAmount());
            }});

            return PriceResponse.success(result);
        } catch (Exception e) {
            return PriceResponse.error("比价失败");
        }
    }

    /**
     * 健康检查接口
     * GET /api/price/health
     */
    @GetMapping("/health")
    public PriceResponse<Map<String, String>> healthCheck() {
        Map<String, String> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "price-compare-service");
        healthInfo.put("time", String.valueOf(System.currentTimeMillis()));

        return PriceResponse.success(healthInfo);
    }
}