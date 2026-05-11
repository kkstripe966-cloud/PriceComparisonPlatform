package com.todayeatapp.blindbox;

import com.todayeatapp.home.entity.Dish;

/**
 * 抽取结果
 */
public class DrawResult {
    private boolean success;
    private Dish dish;
    private String timeMode; // day/night
    private String priceTier; // economy/value/quality/luxury
    private long drawTime; // 抽取时间戳

    public DrawResult() {}

    public DrawResult(boolean success, Dish dish, String timeMode, String priceTier) {
        this.success = success;
        this.dish = dish;
        this.timeMode = timeMode;
        this.priceTier = priceTier;
        this.drawTime = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }

    public String getPriceTier() {
        return priceTier;
    }

    public void setPriceTier(String priceTier) {
        this.priceTier = priceTier;
    }

    public long getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(long drawTime) {
        this.drawTime = drawTime;
    }

    @Override
    public String toString() {
        return "DrawResult{" +
                "success=" + success +
                ", dish=" + dish +
                ", timeMode='" + timeMode + '\'' +
                ", priceTier='" + priceTier + '\'' +
                ", drawTime=" + drawTime +
                '}';
    }
}