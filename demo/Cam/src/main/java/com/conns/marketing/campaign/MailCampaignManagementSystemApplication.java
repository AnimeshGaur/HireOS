package com.conns.marketing.campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.conns.marketing.campaign"})
@EnableJpaRepositories(basePackages = "com.conns.marketing.campaign.template.repository")
public class MailCampaignManagementSystemApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(MailCampaignManagementSystemApplication.class, args);
	}
	
}
