package com.conns.marketing.campaign.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Item {
	
	private String record_count;
	private String execution_id;
	private String file_path;
	private String file_id;
	private String execution_type;
	private String campaign_id;
	private String run_date;

	public String getRecord_count() {
		return record_count;
	}

	public void setRecord_count(String record_count) {
		this.record_count = record_count;
	}

	public String getExecution_id() {
		return execution_id;
	}

	public void setExecution_id(String execution_id) {
		this.execution_id = execution_id;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getExecution_type() {
		return execution_type;
	}

	public void setExecution_type(String execution_type) {
		this.execution_type = execution_type;
	}

	public String getRun_date() {
		return run_date;
	}

	public void setRun_date(String run_date) {
		this.run_date = run_date;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getCampaign_id() {
		return campaign_id;
	}

	public void setCampaign_id(String campaign_id) {
		this.campaign_id = campaign_id;
	}
}
