package com.zjw.common.To;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author zjw
 * @Create 2022/4/11 16:30
 * @Desc
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
