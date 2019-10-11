package com.conns.organization.management.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Store {
	
	@JsonInclude(Include.NON_NULL)
	private Integer regionId;
	private Integer districtId;
	private Integer storeId;
	private Integer storeNbr;
	private String storeLocation;
	private String storeName;
	private String storeManager;
	private Date hireDate;
	private Long retailSf;
	private Long totalSf;
	private String retailRange;
	private Double age;
	private String ageRange;
	private String storeRemodeled;
	private Date storeOpenDate;
	private Date storeCloseDate;
	private String storeType;
	private String addressLine1;
	private String addressLine2;
	private String storePhone;	
	private String storeEmail;
	private String city;
	private String state;
	private Integer zip;
	private String county;
	private Double latitude;
	private Double longitude;
	private String storeRegion;
	private String marketDc;
	private String marketDelivery;
	
	@JsonInclude(Include.NON_NULL)
	private String primaryWarehouse;
	private String serviceWarehouse;
	private String districtAs400;
	private String districtRetailScoreCardReporting;
	private Integer districtRetailScoreCardDmEmpNbr;
	private String districtGeneralReporting;
	private String districtRetailScoreCard;
	private String marketingDistrictAdvertising;
	private String districtQuotaTracker;
	private String marketingDistrict10k;
	private String storeLocationName1;
	private String storeLocationName2;
	private String storeLocationName3;
	
	@JsonInclude(Include.NON_NULL)
	private String storeClassificationYear;
	
	@JsonInclude(Include.NON_NULL)
	private String effectiveStartDate;
	private String storeClassification;
	private String storeClassificationStartDate;
	private String storeClassificationEndDate;
	private Object storeQuotaAmount;

}
