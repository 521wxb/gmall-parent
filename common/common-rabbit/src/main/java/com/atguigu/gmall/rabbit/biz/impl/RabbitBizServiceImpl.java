package com.atguigu.gmall.rabbit.biz.impl;

import com.atguigu.gmall.rabbit.biz.RabbitBizService;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RabbitBizServiceImpl implements RabbitBizService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public void retry(Channel channel, long deliveryTag, String content, int count) {

        // 对content进行md5加密
        String md5 = DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));

        // 构建redis的key
        String redisMsgKey = MqConstant.REDIS_MSG_COUNT + md5;

        // 对Redis中存储的消息的次数加+1
        Long increment = redisTemplate.opsForValue().increment(redisMsgKey);

        // 对结果进行判断
        if(increment > count) {

            // TODO 把消息写入到数据库中

            // TODO 删除redis中消息的重试次数的存储

            // 给rabbitmq返回ack
            try {
                channel.basicAck(deliveryTag , true);
                log.info("重试次数已经超过上限，把消息写到了数据库中...");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {

            // 把消息重新加入到队列中
            try {
                channel.basicNack(deliveryTag , true , true);
                log.error("redis消息的次数是{} ，把消息重新加入到队列中..." , increment);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
