package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 远程服务调用类
 */

//告诉spring cloud这个接口是一个远程客户端，要调用coupon服务(nacos中找到)
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    // 远程服务的url，要一致
    @RequestMapping("coupon/coupon/member/list")
    public R getCouponList();
}
