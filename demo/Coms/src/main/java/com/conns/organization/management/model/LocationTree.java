package com.conns.organization.management.model;

public class LocationTree {
	private String locationUniqueId;
	private String name;
	private String locationCode;
	private Integer level;
	private String locationParentKey;

	public LocationTree() {
		
	}
	
	public LocationTree(String locationUniqueId, String name, String locationCode, Integer level, String locationParentKey) {
		this.locationUniqueId = locationUniqueId;
		this.name = name;
		this.locationCode = locationCode;
		this.level = level;
		this.locationParentKey = locationParentKey;
	}
	
	public LocationTree(String locationUniqueId, String name) {
		this.locationUniqueId = locationUniqueId;
		this.name = name;
	}
	
	public String getLocationUniqueId() {
		return locationUniqueId;
	}

	public void setLocationUniqueId(String locationUniqueId) {
		this.locationUniqueId = locationUniqueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getLocationParentKey() {
		return locationParentKey;
	}

	public void setLocationParentKey(String locationParentKey) {
		this.locationParentKey = locationParentKey;
	}

}
