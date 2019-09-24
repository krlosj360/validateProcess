package com.conecel.tramite.configuration.properties;


import java.io.File;

import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Configuration
public class Log4j2Loader {


	private String log4j2DirectoryFile="C:\\Users\\HITSS\\Documents\\log\\ValidacionProcess\\config\\log4j2.xml";
	//private String log4j2DirectoryFile="/ecommerce/config/log4j2.xml";
	@Bean
	public String init(){	
		String loggerConfig = log4j2DirectoryFile;
		LoggerContext context = LoggerContext.getContext(false);
		File file = new File(loggerConfig);
		log.info("Loading configuration log4j2..");
		context.setConfigLocation(file.toURI());
		log.info("Loaded configuration log4j2 succesfully");
		return "OK";
	}
}
