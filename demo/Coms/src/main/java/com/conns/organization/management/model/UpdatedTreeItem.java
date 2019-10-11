package com.conns.organization.management.model;

public class UpdatedTreeItem {
	private String domain;
	private String parentCommonId;
	private String commonId;
	private Object entity;
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

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

	public Object getEntity() {
		return entity;
	}

	public void setObject(Object entity) {
		this.entity = entity;
	}

}
