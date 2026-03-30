package com.nanqiong.cppcauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nanqiong")
public class CppcAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(CppcAuthApplication.class, args);
	}

}
