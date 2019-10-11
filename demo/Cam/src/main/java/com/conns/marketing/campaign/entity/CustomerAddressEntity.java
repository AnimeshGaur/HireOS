package com.conns.marketing.campaign.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CustomerAddressEntity {

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "CITY")
	private String city;

	@Column(name = "ADDRESS_TYPE")
	private String addressType;

	@Column(name = "ADDRESS_LINE")
	private String addressLine;

	@Column(name = "STATE_PROVINCE")
	private String stateProvince;
}
