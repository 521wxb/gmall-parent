package com.atguigu.gmall.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.threadpool")
public class ThreadPoolProperties {

    private int corePoolSize ;
    private int maximumPoolSize ;
    private int keepAliveTime ;
    private int workQueueSize ;

}
