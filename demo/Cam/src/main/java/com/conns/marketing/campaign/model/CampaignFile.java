package com.conns.marketing.campaign.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "dev_MMCS_CampaignFiles")
public class CampaignFile {
	private String campaignId;
	private String fileId;
	private Item item;

	@DynamoDBHashKey(attributeName = "campaignId")
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	@DynamoDBHashKey(attributeName = "fileId")
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@DynamoDBAttribute(attributeName = "item") 
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
