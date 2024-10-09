package com.junmooo.lingji.config;

import com.junmooo.lingji.interceptor.HttpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    HttpInterceptor httpInterceptor;

    /**
     * 添加Web项目的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //放行登录页，登陆操作，静态资源
        registry.addInterceptor(httpInterceptor).addPathPatterns("/aigc/**").excludePathPatterns("/", "/user/**");;
    }
}