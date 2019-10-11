package com.conns.marketing.campaign.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.marketing.campaign.model.Item;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "MMCS_CAMPAIGNFILES")
public class CampaignFileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID")
	private Long id;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "EXECUTION_ID")
	private String executionId;
	
	@Column(name = "EXECUTION_TYPE")
	private String exectuionType;
	
	@Column(name = "RECORD_COUNT")
	private String recordCount;
	
	@Column(name = "RUN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date runDate;
	
	@Column(name = "CAMPAIGN_ID")
	private String campaignId;
	
	@Column(name = "FILE_ID")
	private String fileId;

	@Type(type = "json")
	@Column(name = "ITEM", columnDefinition = "json")
	private Item item;
	
}
