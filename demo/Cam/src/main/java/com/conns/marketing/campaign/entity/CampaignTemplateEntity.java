package com.conns.marketing.campaign.entity;

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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.marketing.campaign.model.CampaignTemplate;
import com.conns.marketing.campaign.model.EffectiveDates;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "MMCS_CampaignTemplate")
public class CampaignTemplateEntity extends EffectiveDates implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TEMPLATE_ID")
	private String templateId;

	@Column(name = "TEMPLATE_NAME")
	private String templateName;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "ACTIVE")
	private String active;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveEndDate;

	@Column(name = "EFFECTIVE_START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveStartDate;

	@Type(type = "json")
	@Column(name = "TEMPLATE_DATA", columnDefinition = "json")
	private CampaignTemplate campaignTemplate;

}
