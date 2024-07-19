package com.atguigu.gmall.common.interceptor;

import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.common.util.UserAuthUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class FeignClientInterceptor implements RequestInterceptor {

    /**
     * 我们可以从RequestContextHolder对中获取一个线程内所共享的HttpServletRequest对象
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {

        log.info("FeignClientInterceptor.....apply方法执行了....");
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();

        // 给RequestTemplate添加上对应的请求头：userId , userTempId
        if(userAuthInfo.getUserId() != null) {
            template.header("userId" , userAuthInfo.getUserId().toString()) ;
        }
        template.header("userTempId" , userAuthInfo.getUserTempId()) ;

    }

}
