package com.todayeatapp.blindbox;

import com.todayeatapp.home.entity.Dish;
import com.todayeatapp.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 盲盒抽取服务
 */
@Service
public class BlindBoxService {

    @Autowired
    private HomeService homeService;

    private final Random random = new Random();

    /**
     * 随机抽取菜品
     */
    public DrawResult drawRandom(Double minPrice, Double maxPrice) {
        try {
            // 获取所有菜品
            List<Dish> allDishes = homeService.getAllDishes();
            if (allDishes == null || allDishes.isEmpty()) {
                return new DrawResult(false, null, getCurrentTimeMode(), "unknown");
            }

            // 根据价格范围过滤
            List<Dish> filteredDishes = allDishes.stream()
                .filter(dish -> {
                    BigDecimal price = dish.getMinPrice();
                    if (price == null) return true;
                    double priceValue = price.doubleValue();
                    return (minPrice == null || priceValue >= minPrice) && 
                           (maxPrice == null || priceValue <= maxPrice);
                })
                .collect(Collectors.toList());

            if (filteredDishes.isEmpty()) {
                filteredDishes = allDishes; // 如果没有符合价格的，返回所有菜品
            }

            // 随机选择一个菜品
            Dish selectedDish = filteredDishes.get(random.nextInt(filteredDishes.size()));
            String priceTier = determinePriceTier(selectedDish.getMinPrice());

            return new DrawResult(true, selectedDish, getCurrentTimeMode(), priceTier);
        } catch (Exception e) {
            e.printStackTrace();
            return new DrawResult(false, null, getCurrentTimeMode(), "unknown");
        }
    }

    /**
     * 精准匹配抽取菜品
     */
    public DrawResult drawPrecise(String budgetType, List<String> flavorTypes, Double minPrice, Double maxPrice) {
        try {
            // 获取所有菜品
            List<Dish> allDishes = homeService.getAllDishes();
            if (allDishes == null || allDishes.isEmpty()) {
                return new DrawResult(false, null, getCurrentTimeMode(), budgetType != null ? budgetType : "unknown");
            }

            List<Dish> candidates = new ArrayList<>(allDishes);

            // 根据口味过滤
            if (flavorTypes != null && !flavorTypes.isEmpty()) {
                candidates = candidates.stream()
                    .filter(dish -> matchesFlavor(dish, flavorTypes))
                    .collect(Collectors.toList());
            }

            // 根据价格范围过滤
            if (minPrice != null || maxPrice != null) {
                candidates = candidates.stream()
                    .filter(dish -> {
                        BigDecimal price = dish.getMinPrice();
                        if (price == null) return true;
                        double priceValue = price.doubleValue();
                        return (minPrice == null || priceValue >= minPrice) && 
                               (maxPrice == null || priceValue <= maxPrice);
                    })
                    .collect(Collectors.toList());
            }

            if (candidates.isEmpty()) {
                candidates = allDishes; // 如果没有符合条件的，返回所有菜品
            }

            // 根据预算类型排序并选择
            candidates.sort((a, b) -> {
                BigDecimal priceA = a.getMinPrice();
                BigDecimal priceB = b.getMinPrice();
                double valueA = priceA != null ? priceA.doubleValue() : 0.0;
                double valueB = priceB != null ? priceB.doubleValue() : 0.0;
                return Double.compare(valueA, valueB);
            });

            Dish selectedDish = selectByBudgetType(candidates, budgetType);
            String actualPriceTier = budgetType != null ? budgetType : determinePriceTier(selectedDish.getMinPrice());

            return new DrawResult(true, selectedDish, getCurrentTimeMode(), actualPriceTier);
        } catch (Exception e) {
            e.printStackTrace();
            return new DrawResult(false, null, getCurrentTimeMode(), budgetType != null ? budgetType : "unknown");
        }
    }

    /**
     * 根据预算类型选择菜品
     */
    private Dish selectByBudgetType(List<Dish> sortedCandidates, String budgetType) {
        if (budgetType == null || sortedCandidates.isEmpty()) {
            return sortedCandidates.get(random.nextInt(sortedCandidates.size()));
        }

        int size = sortedCandidates.size();
        switch (budgetType.toLowerCase()) {
            case "economy":
                // 选择最便宜的前25%
                return sortedCandidates.get(random.nextInt(Math.max(1, size / 4)));
            case "value":
                // 选择中等价位的50%
                int start = size / 4;
                int end = size * 3 / 4;
                return sortedCandidates.get(start + random.nextInt(Math.max(1, end - start)));
            case "quality":
                // 选择较贵的25%
                int qualityStart = size * 3 / 4;
                return sortedCandidates.get(qualityStart + random.nextInt(Math.max(1, size - qualityStart)));
            case "luxury":
                // 选择最贵的10%
                int luxuryStart = size * 9 / 10;
                return sortedCandidates.get(luxuryStart + random.nextInt(Math.max(1, size - luxuryStart)));
            default:
                return sortedCandidates.get(random.nextInt(size));
        }
    }

    /**
     * 判断菜品是否匹配口味
     */
    private boolean matchesFlavor(Dish dish, List<String> flavorTypes) {
        String dishName = dish.getDishName() != null ? dish.getDishName().toLowerCase() : "";
        List<String> tags = dish.getTagList() != null ? dish.getTagList() : new ArrayList<>();
        String tagsStr = String.join(" ", tags).toLowerCase();

        for (String flavor : flavorTypes) {
            if (flavor == null) continue;
            
            switch (flavor.toLowerCase()) {
                case "spicy":
                    if (dishName.contains("辣") || dishName.contains("麻辣") || 
                        tagsStr.contains("辣") || tagsStr.contains("麻辣")) {
                        return true;
                    }
                    break;
                case "light":
                    if (dishName.contains("清淡") || dishName.contains("素") || dishName.contains("沙拉") || 
                        dishName.contains("轻食") || tagsStr.contains("清淡") || tagsStr.contains("轻食")) {
                        return true;
                    }
                    break;
                case "healthy":
                    if (dishName.contains("轻食") || dishName.contains("沙拉") || dishName.contains("低卡") || 
                        dishName.contains("健康") || tagsStr.contains("健康") || tagsStr.contains("轻食")) {
                        return true;
                    }
                    break;
                case "meat":
                    if (dishName.contains("牛") || dishName.contains("猪") || dishName.contains("鸡") || 
                        dishName.contains("肉") || dishName.contains("排") || dishName.contains("堡") ||
                        tagsStr.contains("牛") || tagsStr.contains("鸡") || tagsStr.contains("猪") || tagsStr.contains("肉")) {
                        return true;
                    }
                    break;
                case "drink":
                    if (dishName.contains("奶茶") || dishName.contains("茶") || dishName.contains("饮") || 
                        dishName.contains("咖啡") || dishName.contains("果汁") || 
                        tagsStr.contains("饮品") || tagsStr.contains("奶茶")) {
                        return true;
                    }
                    break;
                case "tea":
                    if (dishName.contains("下午茶") || dishName.contains("茶") || dishName.contains("甜品") ||
                        tagsStr.contains("甜品") || tagsStr.contains("茶")) {
                        return true;
                    }
                    break;
                case "exotic":
                    if (dishName.contains("寿司") || dishName.contains("披萨") || dishName.contains("汉堡") || 
                        dishName.contains("意面") || dishName.contains("咖喱")) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * 根据价格确定价格档次
     */
    private String determinePriceTier(BigDecimal price) {
        if (price == null) return "unknown";
        
        double priceValue = price.doubleValue();
        if (priceValue <= 20) return "economy";
        else if (priceValue <= 35) return "value";
        else if (priceValue <= 50) return "quality";
        else return "luxury";
    }

    /**
     * 获取当前时间模式
     */
    public String getCurrentTimeMode() {
        LocalTime now = LocalTime.now();
        // 6:00-18:00 为白天，18:00-6:00 为夜晚
        if (now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(18, 0))) {
            return "day";
        } else {
            return "night";
        }
    }

    /**
     * 获取抽取统计信息
     */
    public Map<String, Object> getDrawStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Dish> allDishes = homeService.getAllDishes();
            stats.put("total", allDishes != null ? allDishes.size() : 0);
            stats.put("successRate", 0.95); // 模拟成功率
            stats.put("timeMode", getCurrentTimeMode());
            stats.put("timestamp", System.currentTimeMillis());
            
            // 按价格档次统计
            if (allDishes != null) {
                Map<String, Long> priceStats = allDishes.stream()
                    .collect(Collectors.groupingBy(
                        dish -> determinePriceTier(dish.getMinPrice()),
                        Collectors.counting()
                    ));
                stats.put("priceDistribution", priceStats);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("total", 0);
            stats.put("successRate", 0.0);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
}