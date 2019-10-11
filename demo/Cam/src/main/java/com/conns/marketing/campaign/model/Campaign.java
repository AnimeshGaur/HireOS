package com.conns.marketing.campaign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;;

@Getter
@Setter
@JsonInclude (Include.NON_NULL)
public class Campaign {
	
	private String campaignId;
	private String campaignName;
	private String searchKeyCampaignName;
	private String templateId;
	private String templateName;
	private String scheduledDate;
	private String scheduledBy;
	private String status;
	private String errorDesc;
	private String statusTracker;
	private String auditStatus;
	private String createdDate;
	private String lastModifiedDate;
	private String campaignStartDate;
	private String campaignEndDate;
	private String processThroughDate;
	private String promoCodePrefix;
	private String as400PromoCodeId;
	private String maximumRecordsToBuild;
	private Source sources;
	private Suppression suppressions;
	private TailAddition tailAdditions;
	private List<Filter> filters;
	private ErrorOut systemCampaignOutputFile;
	private ErrorOut sasCampaignOutputFile;
	private ErrorOut printFile;
	private String sortGroup;

	
}
