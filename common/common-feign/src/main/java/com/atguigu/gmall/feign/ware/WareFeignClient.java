package com.atguigu.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * url参数表示的就是服务提供方的地址
 */
@FeignClient(value = "service-ware" , url = "http://localhost:9001")
public interface WareFeignClient {

    /**
     * 调用库存系统，判断某一个商品是否存在库存
     * @return
     */
    @GetMapping(value = "/hasStock")
    public abstract String hasStock(@RequestParam(value = "skuId") Long skuId , @RequestParam(value = "num") Integer num) ;

}
