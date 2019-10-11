package com.conns.organization.management.configuration;

public class AppConfigBean {
	private String treeTable;
	private String locationTable;
	private String personTable;
	private String masterTable;
	private String statusIndex;
	private String categoryIndex;
	private String employeeUniqueIdIndex;
	private String commonIdIndex;
	private String spreadsheetTable;
	private String domainLevelIndex;

	public String getTreeTable() {
		return treeTable;
	}

	public void setTreeTable(String treeTable) {
		this.treeTable = treeTable;
	}

	public String getLocationTable() {
		return locationTable;
	}

	public void setLocationTable(String locationTable) {
		this.locationTable = locationTable;
	}

	public String getPersonTable() {
		return personTable;
	}

	public void setPersonTable(String personTable) {
		this.personTable = personTable;
	}
	
	public String getStatusIndex() {
		return statusIndex;
	}

	public void setStatusIndex(String statusIndex) {
		this.statusIndex = statusIndex;
	}

	public String getCategoryIndex() {
		return categoryIndex;
	}

	public void setCategoryIndex(String categoryIndex) {
		this.categoryIndex = categoryIndex;
	}

	public String getEmployeeUniqueIdIndex() {
		return employeeUniqueIdIndex;
	}

	public void setEmployeeUniqueIdIndex(String employeeUniqueIdIndex) {
		this.employeeUniqueIdIndex = employeeUniqueIdIndex;
	}

	public String getCommonIdIndex() {
		return commonIdIndex;
	}

	public void setCommonIdIndex(String commonIdIndex) {
		this.commonIdIndex = commonIdIndex;
	}

	public String getSpreadsheetTable() {
		return spreadsheetTable;
	}

	public void setSpreadsheetTable(String spreadsheetTable) {
		this.spreadsheetTable = spreadsheetTable;
	}

	public String getDomainLevelIndex() {
		return domainLevelIndex;
	}

	public void setDomainLevelIndex(String domainLevelIndex) {
		this.domainLevelIndex = domainLevelIndex;
	}

	public String getMasterTable() {
		return masterTable;
	}

	public void setMasterTable(String masterTable) {
		this.masterTable = masterTable;
	}
}
