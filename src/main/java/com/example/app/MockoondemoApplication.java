package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.example.domain.adapter")
@SpringBootApplication
public class MockoondemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockoondemoApplication.class, args);
	}

}
