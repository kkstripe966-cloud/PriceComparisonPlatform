package com.todayeatapp.blindbox;

/**
 * 随机抽取请求参数
 */
public class DrawRequest {
    private Double minPrice;
    private Double maxPrice;

    public DrawRequest() {}

    public DrawRequest(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
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
        return "DrawRequest{" +
                "minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                '}';
    }
}