package com.revature.InfoService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfoServiceApplication.class, args);
	}

}
