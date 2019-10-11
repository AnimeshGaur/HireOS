package com.conns.marketing.campaign.model;

import java.util.List;

import com.conns.marketing.campaign.entity.CustomerEntity;

public class InOutErrorFile {
	private List<CustomerEntity> customers;
	private List<CustomerDataFromHive> customerData;

	public List<CustomerEntity> getCustomers() {
		return customers;
	}

	public void setCustomers(List<CustomerEntity> customers) {
		this.customers = customers;
	}

	public List<CustomerDataFromHive> getCustomerData() {
		return customerData;
	}

	public void setCustomerData(List<CustomerDataFromHive> customerData) {
		this.customerData = customerData;
	}
}
