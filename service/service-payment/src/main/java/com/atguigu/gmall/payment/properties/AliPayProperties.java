package com.atguigu.gmall.payment.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay")
public class AliPayProperties {

    private String serverUrl ;
    private String appId ;
    private String privateKey ;
    private String format ;
    private String charSet ;
    private String aliPublicKey ;
    private String signType ;
    private String returnUrl ;
    private String notifyUrl ;

}
