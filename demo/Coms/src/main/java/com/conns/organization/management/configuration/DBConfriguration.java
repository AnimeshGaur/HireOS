package com.conns.organization.management.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties("spring.datasource")
@SuppressWarnings("unused")
public class DBConfriguration {


	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	@Profile("dev")
	@Bean
	public String devDatabaseConnection() {
		return "Development DB is connected";
	}
	
	@Profile("prod")
	@Bean
	public String prodDatabaseConnection() {
		return "Development DB is connected";
	}
	
}
