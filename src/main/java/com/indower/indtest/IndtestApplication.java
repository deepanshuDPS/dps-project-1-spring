package com.indower.indtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class IndtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndtestApplication.class, args);
	}

}
