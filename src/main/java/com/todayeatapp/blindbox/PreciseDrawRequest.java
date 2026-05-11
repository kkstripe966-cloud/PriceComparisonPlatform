package com.todayeatapp.blindbox;

import java.util.List;

/**
 * 精准抽取请求参数
 */
public class PreciseDrawRequest {
    private String budgetType; // 预算类型：economy, value, quality, luxury
    private List<String> flavorTypes; // 口味类型列表
    private Double minPrice;
    private Double maxPrice;

    public PreciseDrawRequest() {}

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public List<String> getFlavorTypes() {
        return flavorTypes;
    }

    public void setFlavorTypes(List<String> flavorTypes) {
        this.flavorTypes = flavorTypes;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public String toString() {
        return "PreciseDrawRequest{" +
                "budgetType='" + budgetType + '\'' +
                ", flavorTypes=" + flavorTypes +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                '}';
    }
}