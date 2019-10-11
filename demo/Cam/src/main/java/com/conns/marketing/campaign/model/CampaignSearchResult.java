package com.conns.marketing.campaign.model;

public class CampaignSearchResult {
	private String campaignId;
	private String campaignName;
	private String scheduledDate;
	private String scheduledBy;
	private String status;
	private String statusTracker;

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public String getScheduledBy() {
		return scheduledBy;
	}

	public void setScheduledBy(String scheduledBy) {
		this.scheduledBy = scheduledBy;
	}

	public String getStatusTracker() {
		return statusTracker;
	}

	public void setStatusTracker(String statusTracker) {
		this.statusTracker = statusTracker;
	}

}
