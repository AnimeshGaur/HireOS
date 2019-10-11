package com.conns.organization.management.model;

public class TreeNode {
	private String parentCommonId;
	private String domain;
	private String position;
	private Integer empLevel;
	private String parentPositionId;
	private String parentEmployeeName;
	private Location location;
	private Integer locLevel;
	private String locationParentKey;
	private String status;
	private Boolean leaf;
	private String category;
	private String type;
	
	public String getParentCommonId() {
		return parentCommonId;
	}
	public void setParentCommonId(String parentCommonId) {
		this.parentCommonId = parentCommonId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getEmpLevel() {
		return empLevel;
	}
	public void setEmpLevel(Integer empLevel) {
		this.empLevel = empLevel;
	}
	public String getParentPositionId() {
		return parentPositionId;
	}
	public void setParentPositionId(String parentPositionId) {
		this.parentPositionId = parentPositionId;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Integer getLocLevel() {
		return locLevel;
	}
	public void setLocLevel(Integer locLevel) {
		this.locLevel = locLevel;
	}
	public String getLocationParentKey() {
		return locationParentKey;
	}
	public void setLocationParentKey(String locationParentKey) {
		this.locationParentKey = locationParentKey;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentEmployeeName() {
		return parentEmployeeName;
	}
	public void setParentEmployeeName(String parentEmployeeName) {
		this.parentEmployeeName = parentEmployeeName;
	}
}
