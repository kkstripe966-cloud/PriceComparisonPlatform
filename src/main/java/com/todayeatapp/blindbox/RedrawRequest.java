package com.todayeatapp.blindbox;

import java.util.List;

/**
 * 重新抽取请求参数
 */
public class RedrawRequest {
    private DrawType drawType; // 抽取类型：RANDOM 或 PRECISE
    private String budgetType; // 预算类型：economy, value, quality, luxury
    private List<String> flavorTypes; // 口味类型列表
    private Double minPrice;
    private Double maxPrice;

    public RedrawRequest() {}

    public DrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(DrawType drawType) {
        this.drawType = drawType;
    }

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
        return "RedrawRequest{" +
                "drawType=" + drawType +
                ", budgetType='" + budgetType + '\'' +
                ", flavorTypes=" + flavorTypes +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                '}';
    }
}