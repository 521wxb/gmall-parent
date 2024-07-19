package com.atguigu.gmall.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.atguigu.gmall.payment.properties.AliPayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliPayConfiguration {

    @Autowired
    private AliPayProperties aliPayProperties ;

    /**
     * 配置AlipayClient对象
     * @return
     */
    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayProperties.getServerUrl() ,
                aliPayProperties.getAppId() ,
                aliPayProperties.getPrivateKey(),
                aliPayProperties.getFormat(),
                aliPayProperties.getCharSet(),
                aliPayProperties.getAliPublicKey(),
                aliPayProperties.getSignType()
        ) ;
        return alipayClient ;
    }

}
