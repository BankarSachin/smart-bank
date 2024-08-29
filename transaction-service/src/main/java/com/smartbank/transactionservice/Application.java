package com.smartbank.transactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync //Not required since I am using spring boot starter actuator which enabled Asynch and Schedulting by default
public class Application {

	public static void main(String[] args){
		SpringApplication.run(Application.class, args);
	}

}
