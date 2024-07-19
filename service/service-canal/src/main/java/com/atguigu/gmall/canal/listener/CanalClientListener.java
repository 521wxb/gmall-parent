package com.atguigu.gmall.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Slf4j
@CanalEventListener   // 声明当前这个类是Canal的监听器
public class CanalClientListener {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    /**
     * @ListenPoint注解加上以后，可以监听插入的操作，修改，删除操作
     * @InsertListenPoint： 只监听插入操作
     * @UpdateListenPoint： 只监听修改操作
     * @DeleteListenPoint： 只监听删除
     */

    /**
     * CanalEntry.EventType eventType: 事件的类型，INSERT ， UPDATE ， DELETE
     * CanalEntry.RowData rowData： 行数据
     * @param eventType
     * @param rowData
     */
    @ListenPoint(schema = "gmall_product" , table = "sku_info")
    public void updateMethod(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {        // 监听方法

        log.info("当前的事件类型为：" + eventType);

        // 获取修改之前数据
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        beforeColumnsList.stream().forEach(column -> {
            String columnName = column.getName();           // 获取到了列名称
            String value = column.getValue();               // 列的值
            log.info("columnName： " + columnName + "-----> columnValue: " + value);
        });

        log.info("------------------------------------------------------------------------------------------------");

        // 获取修改之后的数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        afterColumnsList.stream().forEach(column -> {
            String columnName = column.getName();           // 获取到了列名称
            String value = column.getValue();               // 列的值
            log.info("columnName： " + columnName + "-----> columnValue: " + value);

            // 判断当前遍历的这个列的名称是否是id
            if("id".equals(columnName)) {
                redisTemplate.delete(GmallConstant.REDIS_SKU_DETAIL_PREFIX + value) ;
                log.info("删除了redis中的数据，skuId： {}" , value);
            }

        });

    }

}
