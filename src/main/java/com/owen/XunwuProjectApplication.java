package com.owen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class XunwuProjectApplication {
	//测试git提交代码
	public static void main(String[] args) {
		SpringApplication.run(XunwuProjectApplication.class, args);
	}
	@GetMapping("/hello")
	public String hello(){
		return "Hello Owen";
	}
}
