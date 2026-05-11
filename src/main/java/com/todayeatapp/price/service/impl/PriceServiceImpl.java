package com.todayeatapp.price.service.impl;

import com.todayeatapp.price.dto.PriceCompareDTO;
import com.todayeatapp.price.entity.PlatformPrice;
import com.todayeatapp.price.entity.PriceHistory;
import com.todayeatapp.price.entity.Product;
import com.todayeatapp.price.dao.ProductMapper;
import com.todayeatapp.price.service.PriceService;
import com.todayeatapp.price.util.PriceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private ProductMapper productMapper;

    private final PriceUtil priceUtil = new PriceUtil();

    @Override
    public Product getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public PriceCompareDTO getPriceDetail(Long productId) {
        // 1. 获取商品信息
        Product product = productMapper.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 获取各平台价格
        List<PlatformPrice> prices = productMapper.getPricesByProductId(productId);
        if (prices.isEmpty()) {
            throw new RuntimeException("该商品暂无价格信息");
        }

        // 3. 计算价格信息
        List<PlatformPrice> processedPrices = processPrices(prices);
        PlatformPrice bestChoice = findBestChoice(processedPrices);

        // 4. 构建返回DTO
        PriceCompareDTO dto = new PriceCompareDTO();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductDescription(product.getDescription());
        dto.setProductImage(product.getImageUrl());
        dto.setCategory(product.getCategory());
        dto.setTags(product.getTags());
        dto.setPopularity(product.getPopularity());

        // 转换价格列表
        List<PriceCompareDTO.PlatformPriceDTO> priceDTOs = processedPrices.stream()
                .map(this::convertToPriceDTO)
                .collect(Collectors.toList());
        dto.setPriceList(priceDTOs);

        // 设置最佳选择
        dto.setBestChoice(convertToPriceDTO(bestChoice));

        // 计算统计信息
        calculateStatistics(dto, processedPrices);

        // 价格预警分析
        analyzeWarnings(dto, processedPrices);

        // 获取相似商品推荐
        List<Product> similarProducts = productMapper.getSimilarProducts(productId, 3);
        List<PriceCompareDTO.RecommendProductDTO> recommendations = getProductRecommendations(similarProducts);
        dto.setRecommendations(recommendations);

        // 生成优惠建议
        List<String> suggestions = generateSuggestions(processedPrices, bestChoice);
        dto.setSuggestions(suggestions);

        return dto;
    }

    @Override
    public PriceCompareDTO refreshPrice(Long productId) {
        // 这里可以模拟调用第三方API获取最新价格
        // 实际项目中可以接入美团、饿了么等平台的API

        System.out.println("开始刷新商品 " + productId + " 的价格");

        // 模拟价格更新
        List<PlatformPrice> prices = productMapper.getPricesByProductId(productId);
        Random random = new Random();

        for (PlatformPrice price : prices) {
            // 模拟价格波动
            BigDecimal changePercent = new BigDecimal((random.nextDouble() - 0.5) * 0.1); // ±5%
            BigDecimal newPrice = price.getCurrentPrice()
                    .multiply(BigDecimal.ONE.add(changePercent))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal oldPrice = price.getCurrentPrice();
            price.setCurrentPrice(newPrice);

            // 记录价格历史
            PriceHistory history = new PriceHistory();
            history.setPlatformPriceId(price.getId());
            history.setPrice(newPrice);

            BigDecimal change = newPrice.subtract(oldPrice);
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                history.setChangeType("up");
            } else if (change.compareTo(BigDecimal.ZERO) < 0) {
                history.setChangeType("down");
            } else {
                history.setChangeType("stable");
            }

            if (oldPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = change.abs().divide(oldPrice, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));
                history.setChangePercent(percent);
            }

            // 保存价格更新
            productMapper.updatePrice(price);
            productMapper.insertPriceHistory(history);

            // 重新分析预警
            analyzePriceWarning(price);
        }

        // 重新获取比价详情
        return getPriceDetail(productId);
    }

    @Override
    public PriceCompareDTO.RecommendProductDTO getRecommendations(Long productId) {
        // 获取相似商品
        List<Product> similarProducts = productMapper.getSimilarProducts(productId, 5);

        if (similarProducts.isEmpty()) {
            return null;
        }

        // 转换为推荐DTO
        PriceCompareDTO.RecommendProductDTO recommendation = new PriceCompareDTO.RecommendProductDTO();
        Product firstProduct = similarProducts.get(0);
        recommendation.setId(firstProduct.getId());
        recommendation.setName(firstProduct.getName());
        recommendation.setDescription(firstProduct.getDescription());
        recommendation.setImage(firstProduct.getImageUrl());

        // 获取该商品的最低价格
        List<PlatformPrice> prices = productMapper.getPricesByProductId(firstProduct.getId());
        if (!prices.isEmpty()) {
            PlatformPrice minPrice = prices.stream()
                    .min(Comparator.comparing(PlatformPrice::getCurrentPrice))
                    .orElse(null);
            if (minPrice != null) {
                recommendation.setPrice(minPrice.getCurrentPrice());
                recommendation.setPlatform(minPrice.getPlatformName());

                // 计算价格优势
                List<PlatformPrice> currentProductPrices = productMapper.getPricesByProductId(productId);
                BigDecimal currentMinPrice = currentProductPrices.stream()
                        .map(PlatformPrice::getCurrentPrice)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                if (currentMinPrice.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal advantage = currentMinPrice.subtract(minPrice.getCurrentPrice());
                    recommendation.setPriceAdvantage(advantage);
                    recommendation.setReason(String.format("比当前商品便宜 ¥%.2f", advantage));
                }
            }
        }

        return recommendation;
    }

    @Override
    public Map<String, Object> getPriceTrend(Long productId, String platform, Integer days) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("platform", platform);
        params.put("days", days != null ? days : 7);

        return productMapper.getPriceTrend(params);
    }

    @Override
    public int saveOrUpdatePrice(PlatformPrice price) {
        if (price.getId() == null) {
            return productMapper.insertPrice(price);
        } else {
            return productMapper.updatePrice(price);
        }
    }

    @Override
    public int recordPriceHistory(PriceHistory history) {
        return productMapper.insertPriceHistory(history);
    }

    @Override
    public void analyzePriceWarning(PlatformPrice price) {
        // 获取该商品的所有价格
        List<PlatformPrice> allPrices = productMapper.getPricesByProductId(price.getProductId());

        if (allPrices.size() < 2) {
            price.setWarningLevel(0);
            price.setWarningReason(null);
            return;
        }

        // 计算平均价格
        BigDecimal avgPrice = allPrices.stream()
                .map(PlatformPrice::getCurrentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(allPrices.size()), 2, RoundingMode.HALF_UP);

        // 计算价格差异百分比
        BigDecimal diffPercent = price.getCurrentPrice().subtract(avgPrice)
                .divide(avgPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .abs();

        // 设置预警级别
        if (diffPercent.compareTo(new BigDecimal(30)) > 0) {
            price.setWarningLevel(2);
            price.setWarningReason(String.format("价格比平均高 %.1f%%，可能存在虚高", diffPercent));
        } else if (diffPercent.compareTo(new BigDecimal(15)) > 0) {
            price.setWarningLevel(1);
            price.setWarningReason(String.format("价格比平均高 %.1f%%，请谨慎购买", diffPercent));
        } else {
            price.setWarningLevel(0);
            price.setWarningReason(null);
        }

        // 更新预警信息
        productMapper.updatePrice(price);
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        return productMapper.getHotProducts(limit != null ? limit : 10);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }

    @Override
    public Map<String, Object> calculateBestPlan(Long productId) {
        // 获取商品价格
        List<PlatformPrice> prices = productMapper.getPricesByProductId(productId);

        Map<String, Object> result = new HashMap<>();
        if (prices.isEmpty()) {
            return result;
        }

        // 找出最优价格
        PlatformPrice bestPrice = prices.stream()
                .min(Comparator.comparing(p -> p.getCurrentPrice().add(p.getDeliveryFee())))
                .orElse(null);

        if (bestPrice != null) {
            result.put("bestPlatform", bestPrice.getPlatformName());
            result.put("finalPrice", bestPrice.getCurrentPrice().add(bestPrice.getDeliveryFee()));
            result.put("saveAmount", calculateSaveAmount(prices, bestPrice));
            result.put("recommendation", generateRecommendation(bestPrice, prices));
        }

        return result;
    }

    // 私有方法：处理价格计算
    private List<PlatformPrice> processPrices(List<PlatformPrice> prices) {
        return prices.stream()
                .peek(price -> {
                    // 计算最终支付价
                    BigDecimal deliveryFee = price.getDeliveryFee() != null ? price.getDeliveryFee() : BigDecimal.ZERO;
                    price.setFinalPrice(price.getCurrentPrice().add(deliveryFee));

                    // 计算节省金额
                    if (price.getOriginalPrice() != null &&
                            price.getOriginalPrice().compareTo(price.getCurrentPrice()) > 0) {
                        price.setSaveAmount(price.getOriginalPrice().subtract(price.getCurrentPrice()));
                        price.setSavePercent(price.getSaveAmount()
                                .divide(price.getOriginalPrice(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(100)));
                    } else {
                        price.setSaveAmount(BigDecimal.ZERO);
                        price.setSavePercent(BigDecimal.ZERO);
                    }
                })
                .collect(Collectors.toList());
    }

    private PlatformPrice findBestChoice(List<PlatformPrice> prices) {
        return prices.stream()
                .min(Comparator.comparing(PlatformPrice::getFinalPrice))
                .orElse(prices.get(0));
    }

    private PriceCompareDTO.PlatformPriceDTO convertToPriceDTO(PlatformPrice price) {
        if (price == null) return null;

        PriceCompareDTO.PlatformPriceDTO dto = new PriceCompareDTO.PlatformPriceDTO();
        dto.setId(price.getId());
        dto.setPlatform(price.getPlatform());
        dto.setPlatformName(price.getPlatformName());
        dto.setOriginalPrice(price.getOriginalPrice());
        dto.setCurrentPrice(price.getCurrentPrice());
        dto.setDeliveryFee(price.getDeliveryFee());
        dto.setFinalPrice(price.getFinalPrice());
        dto.setDiscount(price.getDiscount());
        dto.setCouponInfo(price.getCouponInfo());
        dto.setMinOrder(price.getMinOrder());
        dto.setRating(price.getRating());
        dto.setSalesMonth(price.getSalesMonth());
        dto.setSaveAmount(price.getSaveAmount());
        dto.setSavePercent(price.getSavePercent());
        dto.setWarningLevel(price.getWarningLevel());
        dto.setWarningReason(price.getWarningReason());
        dto.setUpdateTime(price.getUpdateTime().toString());
        dto.setBuyUrl(generateBuyUrl(price));

        return dto;
    }

    private String generateBuyUrl(PlatformPrice price) {
        switch (price.getPlatform()) {
            case "meituan":
                return "https://waimai.meituan.com/";
            case "eleme":
                return "https://www.ele.me/";
            case "jd":
                return "https://daojia.jd.com/";
            default:
                return "#";
        }
    }

    private void calculateStatistics(PriceCompareDTO dto, List<PlatformPrice> prices) {
        if (prices.isEmpty()) return;

        // 计算最低价
        PlatformPrice minPrice = prices.stream()
                .min(Comparator.comparing(PlatformPrice::getFinalPrice))
                .orElse(null);

        if (minPrice != null) {
            dto.setLowestPrice(minPrice.getFinalPrice());
            dto.setLowestPricePlatform(minPrice.getPlatformName());
        }

        // 计算平均价格
        BigDecimal avgPrice = prices.stream()
                .map(PlatformPrice::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);
        dto.setAveragePrice(avgPrice);

        // 计算节省金额
        if (minPrice != null && avgPrice != null) {
            dto.setSaveAmount(avgPrice.subtract(minPrice.getFinalPrice()));
            dto.setSavePercent(dto.getSaveAmount()
                    .divide(avgPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100)));
        }
    }

    private void analyzeWarnings(PriceCompareDTO dto, List<PlatformPrice> prices) {
        List<PlatformPrice> warnings = prices.stream()
                .filter(p -> p.getWarningLevel() > 0)
                .collect(Collectors.toList());

        if (!warnings.isEmpty()) {
            dto.setHasWarning(true);
            dto.setMaxWarningLevel(warnings.stream()
                    .mapToInt(PlatformPrice::getWarningLevel)
                    .max()
                    .orElse(0));

            // 生成预警信息
            StringBuilder warningMsg = new StringBuilder("发现价格异常：");
            for (PlatformPrice warning : warnings) {
                warningMsg.append(String.format("%s价格偏高（%s）；",
                        warning.getPlatformName(), warning.getWarningReason()));
            }
            dto.setWarningMessage(warningMsg.toString());
        } else {
            dto.setHasWarning(false);
        }
    }

    private List<PriceCompareDTO.RecommendProductDTO> getProductRecommendations(List<Product> products) {
        return products.stream()
                .map(product -> {
                    PriceCompareDTO.RecommendProductDTO dto = new PriceCompareDTO.RecommendProductDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setImage(product.getImageUrl());

                    // 获取最低价格
                    List<PlatformPrice> prices = productMapper.getPricesByProductId(product.getId());
                    if (!prices.isEmpty()) {
                        PlatformPrice minPrice = prices.stream()
                                .min(Comparator.comparing(PlatformPrice::getCurrentPrice))
                                .orElse(null);
                        if (minPrice != null) {
                            dto.setPrice(minPrice.getCurrentPrice());
                            dto.setPlatform(minPrice.getPlatformName());
                            dto.setReason("相似商品，价格更优");
                        }
                    }

                    return dto;
                })
                .limit(3)  // 最多推荐3个
                .collect(Collectors.toList());
    }

    private List<String> generateSuggestions(List<PlatformPrice> prices, PlatformPrice bestChoice) {
        List<String> suggestions = new ArrayList<>();

        if (bestChoice != null) {
            suggestions.add(String.format("推荐在%s购买，价格最优：¥%.2f（含配送）",
                    bestChoice.getPlatformName(), bestChoice.getFinalPrice()));

            // 满减建议
            if (bestChoice.getMinOrder() != null &&
                    bestChoice.getFinalPrice().compareTo(bestChoice.getMinOrder()) < 0) {
                BigDecimal needMore = bestChoice.getMinOrder().subtract(bestChoice.getFinalPrice());
                suggestions.add(String.format("还差¥%.2f达到起送价，建议凑单", needMore));
            }

            // 优惠券建议
            if (bestChoice.getCouponInfo() != null && !bestChoice.getCouponInfo().isEmpty()) {
                suggestions.add("可使用优惠：" + bestChoice.getCouponInfo());
            }
        }

        // 对比建议
        prices.stream()
                .filter(p -> p.getWarningLevel() > 0)
                .forEach(p -> {
                    suggestions.add(String.format("%s当前价格偏高，建议等待促销", p.getPlatformName()));
                });

        return suggestions;
    }

    private BigDecimal calculateSaveAmount(List<PlatformPrice> prices, PlatformPrice bestPrice) {
        BigDecimal secondBest = prices.stream()
                .filter(p -> !p.getId().equals(bestPrice.getId()))
                .map(p -> p.getCurrentPrice().add(p.getDeliveryFee()))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        if (secondBest.compareTo(BigDecimal.ZERO) > 0) {
            return secondBest.subtract(bestPrice.getCurrentPrice().add(bestPrice.getDeliveryFee()));
        }
        return BigDecimal.ZERO;
    }

    private String generateRecommendation(PlatformPrice bestPrice, List<PlatformPrice> prices) {
        BigDecimal bestFinal = bestPrice.getCurrentPrice().add(bestPrice.getDeliveryFee());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在%s购买最划算，最终支付¥%.2f。",
                bestPrice.getPlatformName(), bestFinal));

        // 添加与其他平台的对比
        prices.stream()
                .filter(p -> !p.getId().equals(bestPrice.getId()))
                .forEach(p -> {
                    BigDecimal otherFinal = p.getCurrentPrice().add(p.getDeliveryFee());
                    BigDecimal save = otherFinal.subtract(bestFinal);
                    if (save.compareTo(BigDecimal.ZERO) > 0) {
                        sb.append(String.format("比%s便宜¥%.2f。", p.getPlatformName(), save));
                    }
                });

        return sb.toString();
    }
}