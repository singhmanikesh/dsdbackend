package com.onesolutions.dsd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DsdApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsdApplication.class, args);
	}

}
