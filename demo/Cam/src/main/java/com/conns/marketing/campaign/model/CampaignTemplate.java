package com.conns.marketing.campaign.model;

import java.util.List;

public class CampaignTemplate {
	private String templateId;
	private String templateName;
	private String createdDate;
	private String createdBy;
	private String lastModifiedDate;
	private String effectiveStartDate;
	private String effectiveEndDate;
	private String promoCodePrefix;
	private String status;
	private Source sources;
	private Suppression suppressions;
	private List<Filter> filters;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getEffectiveEndDate() {
		return effectiveEndDate;
	}
	public void setEffectiveEndDate(String effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	public String getPromoCodePrefix() {
		return promoCodePrefix;
	}
	public void setPromoCodePrefix(String promoCodePrefix) {
		this.promoCodePrefix = promoCodePrefix;
	}
	public Source getSources() {
		return sources;
	}
	public void setSources(Source sources) {
		this.sources = sources;
	}
	public Suppression getSuppressions() {
		return suppressions;
	}
	public void setSuppressions(Suppression suppressions) {
		this.suppressions = suppressions;
	}
	public List<Filter> getFilters() {
		return filters;
	}
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEffectiveStartDate() {
		return effectiveStartDate;
	}
	public void setEffectiveStartDate(String effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}	
}
