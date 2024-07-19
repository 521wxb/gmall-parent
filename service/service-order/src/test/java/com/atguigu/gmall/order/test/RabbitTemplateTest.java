package com.atguigu.gmall.order.test;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;


//@SpringBootTest(classes = OrderApplication.class)
public class RabbitTemplateTest {

//    @Autowired
//    private RabbitTemplate rabbitTemplate ;

//    @Test
//    public void sendMsg() {
//        rabbitTemplate.convertAndSend(MqConstant.ORDER_TIMEOUT_EXCHANGE, "atguigu2","test order service rabbitmq...");
//    }

    public static void main(String[] args) {
        Map<String , Long> map = new HashMap<>() ;
        map.put("userId" , 3L) ;
        map.put("orderId" , 837008833078362112L) ;
        String string = JSON.toJSONString(map);
        System.out.println(string);
    }

}
