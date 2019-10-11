package com.conns.organization.management.model;

public class OrganizationTree {

	private String parentCommonId;
	private String commonId;
	private String domain;
	private String employeeUniqueId;
	private String parentEmployeeName;
	private String locationUniqueId;
	private PersonTree employee;
	private LocationTree location;
	private Boolean leaf;
	private String status;
	private String type;
	private String createdDate;
	private String lastModifiedDate;
	private String empUiStatus;
	private String locUiStatus;
	private String category;

	public String getParentCommonId() {
		return parentCommonId;
	}

	public void setParentCommonId(String parentCommonId) {
		this.parentCommonId = parentCommonId;
	}

	public String getCommonId() {
		return commonId;
	}

	public void setCommonId(String commonId) {
		this.commonId = commonId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmployeeUniqueId() {
		return employeeUniqueId;
	}

	public void setEmployeeUniqueId(String employeeUniqueId) {
		this.employeeUniqueId = employeeUniqueId;
	}

	public String getParentEmployeeName() {
		return parentEmployeeName;
	}

	public void setParentEmployeeName(String parentEmployeeName) {
		this.parentEmployeeName = parentEmployeeName;
	}

	public String getLocationUniqueId() {
		return locationUniqueId;
	}

	public void setLocationUniqueId(String locationUniqueId) {
		this.locationUniqueId = locationUniqueId;
	}

	public PersonTree getEmployee() {
		return employee;
	}

	public void setEmployee(PersonTree employee) {
		this.employee = employee;
	}

	public LocationTree getLocation() {
		return location;
	}

	public void setLocation(LocationTree location) {
		this.location = location;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getEmpUiStatus() {
		return empUiStatus;
	}

	public void setEmpUiStatus(String empUiStatus) {
		this.empUiStatus = empUiStatus;
	}

	public String getLocUiStatus() {
		return locUiStatus;
	}

	public void setLocUiStatus(String locUiStatus) {
		this.locUiStatus = locUiStatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
