package com.conns.marketing.campaign.repository;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataSourceSingleton {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceSingleton.class);
	
	private static DataSourceSingleton instance = null;
	public JsonNode jsonNode = null;
	ApplicationContext applicationContext = new AnnotationConfigApplicationContext();
	
	public static DataSourceSingleton getInstance() throws Exception{
		if(instance == null) {
			instance = new DataSourceSingleton();
		}
		return instance;
	}
	
	private DataSourceSingleton() throws Exception{
		Resource resource = applicationContext.getResource("classpath:datasource.json");
		try {
			JsonParser parser = new JsonFactory().createParser(new File(resource.getFile().getPath()));
			jsonNode = new ObjectMapper().readTree(parser);
		} catch (IOException ex) {
			logger.error("DataSourceSingleton - IOException - Could not parse file. \n", ex);
			throw new Exception();
		}
		
	}
}
