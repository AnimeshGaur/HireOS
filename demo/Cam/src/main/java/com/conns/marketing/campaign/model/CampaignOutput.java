package com.conns.marketing.campaign.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CampaignOutput {
	private String campaignId;
	private String campaignName;
	private Date scheduledDate;
	private String status;
	private String recordCount;

}
