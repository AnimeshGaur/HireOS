package com.conns.organization.management.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CONNS_STORE")
public class StoreEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "STORE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer storeId;

	@NotNull
	@Column(name = "STORE_NBR", nullable = false)
	private Integer storeNbr;

	@NotBlank
	@Column(name = "STORE_NAME", nullable = false)
	private String storeName;

	@NotBlank
	@Column(name = "STORE_LOCATION", nullable = false)
	private String storeLocation;

	@Column(name = "STORE_MANAGER")
	private String storeManager;

	@Column(name = "HIRE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date hireDate;

	@Column(name = "RETAIL_SF")
	private Long retailSf;

	@Column(name = "TOTAL_SF")
	private Long totalSf;

	@Column(name = "RETAIL_RANGE")
	private String retailRange;

	@Column(name = "AGE", precision = 10, scale = 2)
	private Double age;

	@Column(name = "AGE_RANGE")
	private String ageRange;

	@Column(name = "STORE_REMODELED")
	private String storeRemodeled;

	@Column(name = "STORE_OPEN_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date storeOpenDate;

	@Column(name = "STORE_CLOSE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date storeCloseDate;

	@Column(name = "STORE_TYPE")
	private String storeType;

	@Column(name = "ADDRESS_LINE_1")
	private String addressLine1;

	@Column(name = "ADDRESS_LINE_2")
	private String addressLine2;

	@Column(name = "STORE_PHONE")
	private String storePhone;

	@Column(name = "STORE_EMAIL")
	private String storeEmail;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIP")
	private Integer zip;

	@Column(name = "COUNTY")
	private String county;

	@Column(name = "LATITUDE", precision = 10, scale = 2, columnDefinition="Decimal(10,2)")
	private Double latitude;

	@Column(name = "LONGITUDE",  columnDefinition="Decimal(10,2)")
	private Double longitude;

	@Column(name = "STORE_REGION")
	private String storeRegion;

	@Column(name = "MARKET_DC")
	private String marketDc;

	@Column(name = "MARKET_DELIVERY")
	private String marketDelivery;

	@Column(name = "PRIMARY_WAREHOUSE")
	private String primaryWareouse;

	@Column(name = "SERVICE_WAREHOUSE")
	private String serviceWarehouse;

	@Column(name = "DISTRICT_AS400")
	private String districtAs400;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD_REPORTING")
	private String districtRetailScoreCardReporting;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD_DM_EMP_NBR")
	private Integer districtRetailScoreCardDmEmpNbr;

	@Column(name = "DISTRICT_GENERAL_REPORTING")
	private String districtGeneralReporting;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD")
	private String districtRetailScoreCard;

	@Column(name = "MARKETING_DISTRICT_ADVERTISING")
	private String marketingDistrictAdvertising;

	@Column(name = "DISTRICT_QUOTA_TRACKER")
	private String districtQuotaTracker;

	@Column(name = "MARKETING_DISTRICT_10K")
	private String marketingDistrict10k;

	@Column(name = "STORE_LOCATION_NAME_1", nullable = false)
	private String storeLocationName1;

	@Column(name = "STORE_LOCATION_NAME_2")
	private String storeLocationName2;

	@Column(name = "STORE_LOCATION_NAME_3")
	private String storeLocationName3;

}
