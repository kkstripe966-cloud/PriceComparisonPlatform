package com.priceplatform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PriceHistory {
    private Long id;
    private Long platformPriceId;      // 平台价格ID
    private BigDecimal price;          // 价格
    private String changeType;         // 变化类型：up/down/stable
    private BigDecimal changePercent;  // 变化百分比
    private Date recordTime;           // 记录时间
}