package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillFeignClient seckillFeignClient ;

    @GetMapping(value = "/seckill.html")
    public String seckillList(Model model) {
        Result<List<SeckillGoods>> allToDaySeckillGoods = seckillFeignClient.findAllToDaySeckillGoods();
        List<SeckillGoods> data = allToDaySeckillGoods.getData();
        model.addAttribute("list" , data) ;
        return "seckill/index" ;
    }

    @GetMapping(value = "/seckill/{skuId}.html")
    public String seckillItem(@PathVariable(value = "skuId") Long skuId , Model model) {
        Result<SeckillGoods> seckillGoodsBySkuId = seckillFeignClient.findSeckillGoodsBySkuId(skuId);
        SeckillGoods seckillGoods = seckillGoodsBySkuId.getData();
        model.addAttribute("item" , seckillGoods) ;
        return "seckill/item" ;
    }

    @GetMapping(value = "/seckill/queue.html")
    public String queue(@RequestParam(value = "skuId") Long skuId , @RequestParam(value = "skuIdStr") String skuIdStr,
                        Model model) {
        model.addAttribute("skuId" ,skuId) ;
        model.addAttribute("skuIdStr" , skuIdStr) ;
        return "seckill/queue" ;
    }

}
