package com.conns.marketing.campaign.configuration;

public class AppConfigBean {
	private String region;
	private String profileCredentialsProvider;
	private int filterCriteria;
	private int searchCriteria;
	private String nameNode;
	private String masterTable;
	private String campaignTable;
	private String templateTable;
	private String campaignFilesTable;
	private String customerTable;
	private String cmsDpcTable;
	private String hiveUser;
	private String hivePassword;
	private String hiveConnectionUrl;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getProfileCredentialsProvider() {
		return profileCredentialsProvider;
	}

	public void setProfileCredentialsProvider(String profileCredentialsProvider) {
		this.profileCredentialsProvider = profileCredentialsProvider;
	}

	public String getMasterTable() {
		return masterTable;
	}

	public void setMasterTable(String masterTable) {
		this.masterTable = masterTable;
	}

	public String getCampaignTable() {
		return campaignTable;
	}

	public void setCampaignTable(String campaignTable) {
		this.campaignTable = campaignTable;
	}

	public String getTemplateTable() {
		return templateTable;
	}

	public void setTemplateTable(String templateTable) {
		this.templateTable = templateTable;
	}

	public int getFilterCriteria() {
		return filterCriteria;
	}

	public void setFilterCriteria(int filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	public int getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(int searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public String getNameNode() {
		return nameNode;
	}

	public void setNameNode(String nameNode) {
		this.nameNode = nameNode;
	}

	public String getCampaignFilesTable() {
		return campaignFilesTable;
	}

	public void setCampaignFilesTable(String campaignFilesTable) {
		this.campaignFilesTable = campaignFilesTable;
	}

	public String getCustomerTable() {
		return customerTable;
	}

	public void setCustomerTable(String customerTable) {
		this.customerTable = customerTable;
	}

	public String getCmsDpcTable() {
		return cmsDpcTable;
	}

	public void setCmsDpcTable(String cmsDpcTable) {
		this.cmsDpcTable = cmsDpcTable;
	}

	public String getHiveUser() {
		return hiveUser;
	}

	public void setHiveUser(String hiveUser) {
		this.hiveUser = hiveUser;
	}

	public String getHivePassword() {
		return hivePassword;
	}

	public void setHivePassword(String hivePassword) {
		this.hivePassword = hivePassword;
	}

	public String getHiveConnectionUrl() {
		return hiveConnectionUrl;
	}

	public void setHiveConnectionUrl(String hiveConnectionUrl) {
		this.hiveConnectionUrl = hiveConnectionUrl;
	}

}
