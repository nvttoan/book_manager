package com.toan.spring.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class projectApplication {

	public static void main(String[] args) {
		SpringApplication.run(projectApplication.class, args);
	}

}
