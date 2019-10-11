package com.conns.marketing.campaign.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@PropertySource("application.properties")
public class AppConfig {
	
	@Value("${aws.dynamodb.region}")
	private String region;
	
	@Value("${aws.dynamodb.profile}")
	private String profileCredentialsProvider;
	
	@Value("${filter.criteria}")
	private int filterCriteria;
	
	@Value("${search.criteria}")
	private int searchCriteria;
	
	@Value("${name.node}")
	private String nameNode;
	
	@Value("${dynamodb.table.master}")
	private String masterTable;
	
	@Value("${dynamodb.table.campaign}")
	private String campaignTable;
	
	@Value("${dynamodb.table.template}")
	private String templateTable;
	
	@Value("${dynamodb.table.campaignfiles}")
	private String campaignFilesTable;
	
	@Value("${dynamodb.table.customer}")
	private String customerTable;
	
	@Value("${hive.table.cms.dpc}")
	private String cmsDpcTable;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public AppConfigBean getProfile() {
		AppConfigBean appConfigBean = new AppConfigBean();
		appConfigBean.setCampaignTable(campaignTable);
		appConfigBean.setMasterTable(masterTable);
		appConfigBean.setTemplateTable(templateTable);
		appConfigBean.setCampaignFilesTable(campaignFilesTable);
		appConfigBean.setCustomerTable(customerTable);
		appConfigBean.setProfileCredentialsProvider(profileCredentialsProvider);
		appConfigBean.setRegion(region);
		appConfigBean.setFilterCriteria(filterCriteria);
		appConfigBean.setSearchCriteria(searchCriteria);
		appConfigBean.setNameNode(nameNode);
		appConfigBean.setCmsDpcTable(cmsDpcTable);
		return appConfigBean;
	}
	
}
