package com.priceplatform.util;

import com.priceplatform.entity.PlatformPrice;
import com.priceplatform.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 价格计算工具类
 */
public class PriceUtil {

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.#%");

    /**
     * 计算最终支付价格（含配送费）
     */
    public BigDecimal calculateFinalPrice(PlatformPrice price) {
        BigDecimal currentPrice = price.getCurrentPrice() != null ? price.getCurrentPrice() : BigDecimal.ZERO;
        BigDecimal deliveryFee = price.getDeliveryFee() != null ? price.getDeliveryFee() : BigDecimal.ZERO;

        return currentPrice.add(deliveryFee).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算节省金额
     */
    public BigDecimal calculateSaveAmount(PlatformPrice price) {
        if (price.getOriginalPrice() == null || price.getCurrentPrice() == null) {
            return BigDecimal.ZERO;
        }

        if (price.getOriginalPrice().compareTo(price.getCurrentPrice()) > 0) {
            return price.getOriginalPrice().subtract(price.getCurrentPrice())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 计算节省百分比
     */
    public BigDecimal calculateSavePercent(PlatformPrice price) {
        BigDecimal saveAmount = calculateSaveAmount(price);

        if (saveAmount.compareTo(BigDecimal.ZERO) > 0 && price.getOriginalPrice() != null) {
            return saveAmount.divide(price.getOriginalPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 格式化价格显示
     */
    public String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0.00";
        }
        return String.format("¥%.2f", price);
    }

    /**
     * 格式化百分比显示
     */
    public String formatPercent(BigDecimal percent) {
        if (percent == null) {
            return "0%";
        }
        return String.format("%.1f%%", percent);
    }

    /**
     * 计算价格平均值
     */
    public BigDecimal calculateAveragePrice(List<PlatformPrice> prices) {
        if (prices == null || prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = prices.stream()
                .map(this::calculateFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算价格标准差（衡量价格差异程度）
     */
    public BigDecimal calculatePriceStd(List<PlatformPrice> prices) {
        if (prices == null || prices.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal avgPrice = calculateAveragePrice(prices);

        BigDecimal variance = prices.stream()
                .map(price -> calculateFinalPrice(price).subtract(avgPrice))
                .map(diff -> diff.multiply(diff))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);

        return sqrt(variance).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算价格波动率（标准差/平均值）
     */
    public BigDecimal calculatePriceVolatility(List<PlatformPrice> prices) {
        BigDecimal avgPrice = calculateAveragePrice(prices);
        BigDecimal stdDev = calculatePriceStd(prices);

        if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
            return stdDev.divide(avgPrice, 4, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 分析价格预警
     */
    public Map<String, Object> analyzePriceWarning(PlatformPrice price, List<PlatformPrice> allPrices) {
        Map<String, Object> result = new HashMap<String, Object>();

        BigDecimal currentPrice = calculateFinalPrice(price);
        BigDecimal avgPrice = calculateAveragePrice(allPrices);
        BigDecimal stdDev = calculatePriceStd(allPrices);

        // 计算价格偏离度
        BigDecimal deviation = BigDecimal.ZERO;
        if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
            deviation = currentPrice.subtract(avgPrice)
                    .divide(avgPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .abs();
        }

        // 设置预警级别
        int warningLevel = 0;
        String warningReason = null;

        if (deviation.compareTo(new BigDecimal(30)) > 0) {
            warningLevel = 2;  // 严重警告
            warningReason = String.format("价格偏离平均价格%.1f%%", deviation);
        } else if (deviation.compareTo(new BigDecimal(15)) > 0) {
            warningLevel = 1;  // 一般警告
            warningReason = String.format("价格偏离平均价格%.1f%%", deviation);
        }

        // 检查价格是否为最高
        BigDecimal maxPrice = allPrices.stream()
                .map(this::calculateFinalPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        if (currentPrice.compareTo(maxPrice) == 0 && allPrices.size() > 1) {
            warningLevel = Math.max(warningLevel, 1);
            warningReason = warningReason != null ?
                    warningReason + "，当前为最高价格" : "当前为最高价格";
        }

        result.put("warningLevel", warningLevel);
        result.put("warningReason", warningReason);
        result.put("deviation", deviation);
        result.put("avgPrice", avgPrice);
        result.put("stdDev", stdDev);
        result.put("isHighest", currentPrice.compareTo(maxPrice) == 0);

        return result;
    }

    /**
     * 生成价格描述
     */
    public String generatePriceDescription(PlatformPrice price) {
        StringBuilder description = new StringBuilder();

        description.append(formatPrice(calculateFinalPrice(price)));

        if (price.getDeliveryFee() != null && price.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0) {
            description.append("（含配送费").append(formatPrice(price.getDeliveryFee())).append("）");
        }

        if (price.getOriginalPrice() != null &&
                price.getOriginalPrice().compareTo(price.getCurrentPrice()) > 0) {
            BigDecimal savePercent = calculateSavePercent(price);
            description.append("，节省").append(formatPercent(savePercent));
        }

        return description.toString();
    }

    /**
     * 比较价格并给出建议
     */
    public Map<String, Object> compareAndSuggest(PlatformPrice currentPrice, PlatformPrice bestPrice) {
        Map<String, Object> result = new HashMap<>();

        BigDecimal currentFinal = calculateFinalPrice(currentPrice);
        BigDecimal bestFinal = calculateFinalPrice(bestPrice);

        if (currentFinal.compareTo(bestFinal) > 0) {
            BigDecimal saveAmount = currentFinal.subtract(bestFinal);
            BigDecimal savePercent = saveAmount.divide(currentFinal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));

            result.put("suggestion", "建议在" + bestPrice.getPlatformName() + "购买更划算");
            result.put("saveAmount", formatPrice(saveAmount));
            result.put("savePercent", formatPercent(savePercent));
            result.put("isBetter", true);
        } else {
            result.put("suggestion", "当前已是最优价格");
            result.put("isBetter", false);
        }

        result.put("currentPrice", formatPrice(currentFinal));
        result.put("bestPrice", formatPrice(bestFinal));

        return result;
    }

    /**
     * 根据商品列表推荐最优商品
     */
    public Map<String, Object> recommendBestProduct(List<Product> products) {
        Map<String, Object> result = new HashMap<>();

        if (products == null || products.isEmpty()) {
            result.put("message", "暂无商品数据");
            return result;
        }

        // 这里可以添加更复杂的推荐算法
        // 简单实现：选择人气最高且价格最低的商品

        products.forEach(product -> {
            // 获取该商品的最低价格
            // 这里需要根据实际业务逻辑实现
        });

        return result;
    }

    /**
     * 生成优惠券使用建议
     */
    public List<String> generateCouponSuggestions(PlatformPrice price) {
        List<String> suggestions = new ArrayList<String>();

        if (price.getMinOrder() != null) {
            BigDecimal current = calculateFinalPrice(price);
            if (current.compareTo(price.getMinOrder()) < 0) {
                BigDecimal needMore = price.getMinOrder().subtract(current);
                suggestions.add(String.format("还差%s达到起送价，建议凑单", formatPrice(needMore)));
            }
        }

        if (price.getCouponInfo() != null && !price.getCouponInfo().isEmpty()) {
            suggestions.add("可使用优惠：" + price.getCouponInfo());
        }

        // 根据价格区间给出建议
        if (price.getCurrentPrice().compareTo(new BigDecimal("50")) < 0) {
            suggestions.add("小金额订单，可使用小额优惠券");
        } else if (price.getCurrentPrice().compareTo(new BigDecimal("100")) > 0) {
            suggestions.add("大金额订单，可叠加使用多重优惠");
        }

        return suggestions;
    }

    /**
     * 计算平方根（辅助函数）
     */
    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("不能计算负数的平方根");
        }

        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal guess = value.divide(new BigDecimal(2), 10, RoundingMode.HALF_UP);
        BigDecimal lastGuess;

        do {
            lastGuess = guess;
            guess = value.divide(guess, 10, RoundingMode.HALF_UP);
            guess = guess.add(lastGuess);
            guess = guess.divide(new BigDecimal(2), 10, RoundingMode.HALF_UP);
        } while (lastGuess.subtract(guess).abs().compareTo(new BigDecimal("0.0000000001")) > 0);

        return guess.setScale(2, RoundingMode.HALF_UP);
    }
}