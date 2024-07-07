package com.craneresigned.usercenterback.config;

import com.craneresigned.usercenterback.config.filter.RepeatedRequestBodyFilter;
import com.craneresigned.usercenterback.config.interceptor.UserLoginInterceptor;
import com.craneresigned.usercenterback.config.interceptor.UserRegisterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户配置类
 *
 * @Author Crane Resigned
 * @Date 2024/6/23 18:57:36
 */
@Configuration
public class UserWebConfig implements WebMvcConfigurer {

//    @Autowired
//    private RepeatedRequestBodyFilter repeatedRequestBodyFilter;
//
//    @Bean
//    public FilterRegistrationBean registerRepeatedRequest() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(repeatedRequestBodyFilter);
//        registrationBean.addUrlPatterns("/user");
//        registrationBean.setName("repeatedRequestBodyFilter");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserRegisterInterceptor())
                .addPathPatterns("/user/register");
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/login");
    }

}
