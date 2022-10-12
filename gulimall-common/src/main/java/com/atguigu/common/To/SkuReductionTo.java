package com.atguigu.common.To;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author zjw
 * @Create 2022/4/12 10:44
 * @Desc
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
