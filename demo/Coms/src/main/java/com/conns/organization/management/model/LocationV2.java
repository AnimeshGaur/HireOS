package com.conns.organization.management.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocationV2 {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("locationGuid")
	private String locationGUID;
	
	@JsonProperty("locationNbr")
	private Integer locationNbr;
	
	@JsonProperty("storeName")
	private String storeName;
	
	@JsonProperty("locationName1")
	private String locationName1;
	
	@JsonProperty("locationName2")
	private String infoLocationName2;
	
	@JsonProperty("locationName3")
	private String infoLocationName3;
	
	@JsonProperty("co")
	private String infoCo;
	
	@JsonProperty("rvp")
	private String infoRvp;
	
	@JsonProperty("district")
	private String infoDistrict;
	
	@JsonProperty("disrictManager")
	private String infoDisrictManager;
	
	@JsonProperty("dmHireDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date infoDmHireDate;
	
	@JsonProperty("storeManager")
	private String infoStoreManager;
	
	@JsonProperty("smHireDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date infoSmHireDate;
	
	@JsonProperty("retailSf")
	private Integer infoRetailSf;
	
	@JsonProperty("totalSf")
	private Integer infoTotalSf;
	
	@JsonProperty("retailRange")
	private String infoRetailRange;
	
	@JsonProperty("age")
	private Double infoAge;
	
	@JsonProperty("ageRange")
	private String infoAgeRange;
	
	@JsonProperty("remodeled")
	private String infoRemodeled;
	
	@JsonProperty("status")
	private Integer infoStatus;
	
	@JsonProperty("storeStartDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date storeStartDate;
	
	@JsonProperty("storeEffectiveDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date storeEffectiveDate;
	
	@JsonProperty("storeCloseDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date storeCloseDate;
	
	@JsonProperty("districtAS400")
	private String infoDistrictAS400;
	
	@JsonProperty("districtRetailScoreCardReporting")
	private String infoDistrictRetailScoreCardReporting;
	
	@JsonProperty("scoreCardDMScoreNumber")
	private Integer infoScoreCardDMScoreNumber;
	
	@JsonProperty("districtGeneralReporting")
	private String infoDistrictGeneralReporting;
	
	@JsonProperty("districtRetailScoreCard")
	private String infoDistrictRetailScoreCard;
	
	@JsonProperty("districtAdvertizing")
	private String infoDistrictAdvertizing;
	
	@JsonProperty("districtQuotaTracker")
	private String infoDistrictQuotaTracker;
	
	@JsonProperty("district10K")
	private String infoDistrict10K;
	
	@JsonProperty("primaryWarehouse")
	private String infoPrimaryWarehouse;
	
	@JsonProperty("primaryService")
	private String infoPrimaryService;
	
	@JsonProperty("classification")
	private String classification;
	
	@JsonProperty("addressLine1")
	private String addressLine1;
	
	@JsonProperty("addressLine2")
	private String addressLine2;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("zip")
	private Integer zip;
	
	@JsonProperty("country")
	private String infoCountry;
	
	@JsonProperty("latitude")
	private Double infoLatitude;
	
	@JsonProperty("region")
	private String region;
	
	@JsonProperty("delivery")
	private String delivery;
	
	@JsonProperty("udpatedStatus")
	private String udpatedStatus;
	
	@JsonProperty("lastModifiedDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date lastModifiedDate;
	
	@JsonProperty("dc")
	private String dc;
	
	@JsonProperty("longitude")
	private Double infoLongitude;
	
	

}
