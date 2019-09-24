package com.conecel.tramite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.conecel.tramite.configuration.properties.InvokeProperties;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableScheduling
@EnableCircuitBreaker
@ComponentScan(basePackages = {"com.conecel.tramite.repository","com.conecel.tramite.services","com.conecel.tramite.controller","com.conecel.tramite.configuration.properties",
		"com.conecel.tramite","com.conecel.tramite.business"})
public class ValidateProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidateProcessApplication.class, args);
		InvokeProperties.setPathFile("C:\\Users\\HITSS\\Documents\\ecommerce\\service\\ValidateProcess\\Desarrollo\\validateProcess-desarrollo\\src\\main\\resources\\application.properties", true);
		//InvokeProperties.setPathFile("/ecommerce/config/application.properties", true);

	}

}
