package com.atguigu.gmall.product.task;

import com.atguigu.gmall.product.service.BloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component  // 把该类纳入到spring容器中
public class ResetBloomFilterTimeTask {

    @Autowired
    private BloomFilterService bloomFilterService ;

    /**
     * ?：表示的意思是废弃到这一位
     */
    @Scheduled(cron = "0 0 3 */7 * ?")  // cron属性需要一个cron表达式，这个cron表达式用来指定方法在执行的时候时间的规则
    public void resetBloomFilterTask() {
        bloomFilterService.resetBloomFilter();
        log.info("布隆过期的重置方法执行了.....");
    }

}
