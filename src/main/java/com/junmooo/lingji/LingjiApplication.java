package com.junmooo.lingji;

import com.alibaba.dashscope.utils.Constants;
import com.junmooo.lingji.controller.WSServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.junmooo.lingji")
@MapperScan("com.junmooo.lingji.mapper")
public class LingjiApplication {

	public static void main(String[] args) {
		Constants.apiKey="sk-40f540e96272456288ff6890c06d9913";
		SpringApplication springApplication = new SpringApplication(LingjiApplication.class);
		ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);

		//解决WebSocket不能注入的问题
		WSServer.setApplicationContext(configurableApplicationContext);
	}
}
