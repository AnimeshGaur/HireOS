package com.conns.organization.management.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
	private String locationUniqueId; // GUID
	private String locationCode; // Lawson
	private String domain;
	private String entityLocationCode;
	private String name;
	private String locationType;
	private Address primaryAddress;
	private String phone;
	private String email;
	private String market;
	private String createdDate;
	private String lastModifiedDate;
	private String openDate;
	private String closeDate;
	private String updated;
	private String squareFootage;
	private String servedByWhse;
	private KeyMetrics keyMetrics;
	private List<KeyValue> photos;
	private String uiStatus;

	
}
