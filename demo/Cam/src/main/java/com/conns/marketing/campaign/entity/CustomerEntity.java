package com.conns.marketing.campaign.entity;

import java.io.Serializable;

import javax.jdo.annotations.Embedded;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MMCS_CUSTOMER_MDM")
@Getter
@Setter
public class CustomerEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "POSTAL_CODE")
	private String postalCode;

	@Column(name = "UNIQUE_CID")
	private String uniqueCID;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "city", column = @Column(name = "CITY")),
			@AttributeOverride(name = "country", column = @Column(name = "COUNTRY")),
			@AttributeOverride(name = "addressType", column = @Column(name = "ADDRESS_TYPE")),
			@AttributeOverride(name = "addressLine", column = @Column(name = "ADDRESS_LINE")),
			@AttributeOverride(name = "stateProvince", column = @Column(name = "STATE_PROVINCE")) })
	private CustomerAddressEntity address;

	@Column(name = "DATE_OF_BIRTH")
	private String dateOfBirth;

	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;
}
