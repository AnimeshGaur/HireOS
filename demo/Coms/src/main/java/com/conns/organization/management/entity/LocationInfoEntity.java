package com.conns.organization.management.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MISCELLANEOUS_LOCATION_INFO")
public class LocationInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "LOCATION_NBR")
	private Integer locationNbr;
	
	@Column(name = "LOCATION_NAME_2")
	private String locationName2;

	@Column(name = "LOCATION_NAME_3")
	private String locationName3;

	@Column(name = "RETAIL_SF")
	private Integer retailSf;

	@Column(name = "TOTAL_SF")
	private Integer totalSf;

	@Column(name = "RETAIL_RANGE")
	private String retailRange;

	@Column(name = "AGE", columnDefinition = "Decimal(10,2)")
	private Double age;

	@Column(name = "AGE_RANGE")
	private String ageRange;

	@Column(name = "REMODELED")
	private String remodeled;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "CO")
	private String co;

	@Column(name = "RVP")
	private String rvp;

	@Column(name = "DISTRICT")
	private String district;

	@Column(name = "DISTRICT_MANAGER")
	private String disrictManager;

	@Column(name = "DM_HIRE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dmHireDate;

	@Column(name = "STORE_MANAGER")
	private String storeManager;

	@Column(name = "SM_HIRE_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date smHireDate;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD_REPORTING")
	private String districtRetailScoreCardReporting;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD")
	private String districtRetailScoreCard;

	@Column(name = "DISTRICT_ADVERTISING")
	private String districtAdvertizing;

	@Column(name = "DISTRICT_QUOTA_TRACKER")
	private String districtQuotaTracker;

	@Column(name = "DISTRICT_AS400")
	private String districtAS400;

	@Column(name = "DISTRICT_RETAIL_SCORE_CARD_DM_EMP_NBR")
	private Integer scoreCardDMScoreNumber;

	@Column(name = "DISTRICT_GENERAL_REPORTING")
	private String districtGeneralReporting;

	@Column(name = "DISTRICT_10K")
	private String district10K;

	@Column(name = "PRIMARY_WAREHOUSE")
	private String primaryWarehouse;

	@Column(name = "PRIMARY_SERVICE")
	private String primaryService;

	@Column(name = "COUNTY")
	private String county;

	@Column(name = "LATITUDE")
	private Double latitude;

	@Column(name = "LONGITUDE")
	private Double longitude;

	@Column(name = "LAST_MODIFIED_DATE", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_GUID", referencedColumnName = "LOCATION_GUID")
    private LocationMasterEntity locationMaster;

}
