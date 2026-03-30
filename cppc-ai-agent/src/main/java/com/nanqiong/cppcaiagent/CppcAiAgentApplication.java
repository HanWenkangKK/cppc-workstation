package com.nanqiong.cppcaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nanqiong")
public class CppcAiAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(CppcAiAgentApplication.class, args);
	}

}
