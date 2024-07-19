package com.atguigu.gmall.seckill.vo;

import lombok.Data;

@Data
public class SeckillQueueMsg {

    private Long userId ;
    private Long skuId ;
    private String seckillCode ;   // 秒杀码

}
