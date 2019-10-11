package com.conns.marketing.campaign.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "dev_MMCS_CustomerMDM")
public class CustomerAddress {
	private String postalCode;
	private String country;
	private String city;
	private String addressType;
	private String county;
	private String addressLine1;
	private String stateProvince;
	
	@DynamoDBHashKey(attributeName = "postalCode")
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	//@DynamoDBAttribute(attributeName = "country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	//@DynamoDBAttribute(attributeName = "city")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	//@DynamoDBAttribute(attributeName = "addressType")
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	
	//@DynamoDBAttribute(attributeName = "county")
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	
	//@DynamoDBAttribute(attributeName = "addressLine1")
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	//@DynamoDBAttribute(attributeName = "stateProvince")
	public String getStateProvince() {
		return stateProvince;
	}
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}
}
