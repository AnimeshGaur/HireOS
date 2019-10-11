package com.conns.organization.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.conns.organization.management" })
public class ConnsOrganizationManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnsOrganizationManagementApplication.class, args);
	}

}
