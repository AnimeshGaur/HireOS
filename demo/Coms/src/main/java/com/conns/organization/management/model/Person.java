package com.conns.organization.management.model;

import java.util.List;

public class Person {
	private String employeeUniqueId; // GUID
	private String employeeId; // Lawson
	private String firstName;
	private String middleName;
	private String lastName;
	private String domain; // Department
	private String roleEntity;
	private String email;
	private String employeeStatus;
	private String jobCode;
	private String jobCodeDescription;
	private String employmentType;
	private List<KeyValue> photos;
	private String phone;
	private String locationCode;
	private String locationDescription;
	private String createdDate;
	private String lastModifiedDate;
	private String uiStatus;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRoleEntity() {
		return roleEntity;
	}

	public void setRoleEntity(String roleEntity) {
		this.roleEntity = roleEntity;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getJobCodeDescription() {
		return jobCodeDescription;
	}

	public void setJobCodeDescription(String jobCodeDescription) {
		this.jobCodeDescription = jobCodeDescription;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public List<KeyValue> getPhotos() {
		return photos;
	}

	public void setPhotos(List<KeyValue> photos) {
		this.photos = photos;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

	public String getEmployeeUniqueId() {
		return employeeUniqueId;
	}

	public void setEmployeeUniqueId(String employeeUniqueId) {
		this.employeeUniqueId = employeeUniqueId;
	}

	public String getUiStatus() {
		return uiStatus;
	}

	public void setUiStatus(String uiStatus) {
		this.uiStatus = uiStatus;
	}
}
