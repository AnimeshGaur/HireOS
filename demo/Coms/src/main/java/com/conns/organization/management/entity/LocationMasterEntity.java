package com.conns.organization.management.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "LOCATION_MASTER")
public class LocationMasterEntity {

	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Id
	@Column(name = "LOCATION_GUID", unique = true, nullable = false, columnDefinition = "varchar(500)")
	private String locationGUID;

	@Column(name = "LOCATION_NBR")
	private Integer locationNbr;

	@Column(name = "STORE_NAME")
	private String storeName;

	@Column(name = "ADDRESS_LINE_1")
	private String addressLine1;

	@Column(name = "ADDRESS_LINE_2")
	private String addressLine2;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIP")
	private String zip;

	@Column(name = "REGION")
	private String region;

	@Column(name = "LOCATION_NAME_1")
	private String locationName1;

	@Column(name = "STORE_START_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date storeStartDate;

	@Column(name = "STORE_EFFECTIVE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date storeEffectiveDate;

	@Column(name = "STORE_CLOSE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date storeCloseDate;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "DC")
	private String dc;

	@Column(name = "DELIVERY")
	private String delivery;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "UPDATED_STATUS")
	private String udpatedStatus;

	@Column(name = "LAST_MODIFIED_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "locationMaster", fetch = FetchType.EAGER)
	private LocationInfoEntity info;

}
