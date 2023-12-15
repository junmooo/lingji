package com.junmooo.lingji.config;

import com.alibaba.dashscope.utils.Constants;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Apikey {
    @PostConstruct
    public void init() {
        // 执行只执行一次的初始化任务
        Constants.apiKey="sk-40f540e96272456288ff6890c06d9913";
        System.out.println("执行只执行一次的初始化任务");
    }

}
