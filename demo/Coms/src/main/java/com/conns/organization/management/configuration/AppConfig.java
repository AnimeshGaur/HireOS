package com.conns.organization.management.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@PropertySource("application.properties")
public class AppConfig {

	@Value("${dynamodb.table.tree}")
	private String treeTable;
	
	@Value("${dynamodb.table.location}")
	private String locationTable;
	
	@Value("${dynamodb.table.person}")
	private String personTable;
	
	@Value("${dynamodb.table.master}")
	private String masterTable;

	@Value("${dynamodb.index.status-index}")
	private String statusIndex;
	
	@Value("${dynamodb.index.category-index}")
	private String categoryIndex;
	
	@Value("${dynamodb.index.employeeUniqueId-index}")
	private String employeeUniqueIdIndex;
	
	@Value("${dynamodb.index.commonId-index}")
	private String commonIdIndex;
	
	@Value("${dynamodb.table.spreadsheet}")
	private String spreadsheetTable;
	
	@Value("${dynamodb.index.domain-level-index}")
	private String domainLevelIndex;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public AppConfigBean getProfile() {
		AppConfigBean appConfigBean = new AppConfigBean();
		appConfigBean.setTreeTable(treeTable);
		appConfigBean.setLocationTable(locationTable);
		appConfigBean.setPersonTable(personTable);
		appConfigBean.setMasterTable(masterTable);
		appConfigBean.setStatusIndex(statusIndex);
		appConfigBean.setCategoryIndex(categoryIndex);
		appConfigBean.setEmployeeUniqueIdIndex(employeeUniqueIdIndex);
		appConfigBean.setCommonIdIndex(commonIdIndex);
		appConfigBean.setSpreadsheetTable(spreadsheetTable);
		appConfigBean.setDomainLevelIndex(domainLevelIndex);
		return appConfigBean;
	}
	
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
	
}
