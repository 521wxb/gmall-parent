package com.atguigu.gmall.gateway.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.auth")
public class AuthUrlProperties {

    private List<String> noauthurl ;        // 定义不需要校验用户登录的资源路径规则
    private List<String> authurl ;          // 需要验证登录的资源路径的规则
    private String loginPageUrl ;           // 登录页面的url
}
