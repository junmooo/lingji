package com.junmooo.lingji;

import com.alibaba.dashscope.utils.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LingjiApplication {

	public static void main(String[] args) {
		Constants.apiKey="sk-40f540e96272456288ff6890c06d9913";
		SpringApplication.run(LingjiApplication.class, args);
	}

}
