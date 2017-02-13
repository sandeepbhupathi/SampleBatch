package com.sandeep;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class SampleBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleBatchApplication.class, args);
	}
}
