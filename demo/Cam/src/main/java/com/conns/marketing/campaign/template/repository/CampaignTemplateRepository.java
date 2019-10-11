package com.conns.marketing.campaign.template.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.CampaignTemplateEntity;
import com.conns.marketing.campaign.model.CampaignTemplate;

@Repository
public interface CampaignTemplateRepository extends CrudRepository<CampaignTemplateEntity, Long> {

	@Query(value = "FROM CampaignTemplateEntity WHERE active = ?1 AND effectiveStartDate <= ?2 ")
	List<CampaignTemplateEntity> findByStatusAndEffectiveStartDate(String status, Date date);

	@Query(value = "SELECT campaignTemplate FROM CampaignTemplateEntity WHERE templateId = ?1 ")
	CampaignTemplate findByTemplateId(String templateId);

	@Query(value = "FROM CampaignTemplateEntity WHERE templateId = ?1 ")
	CampaignTemplateEntity findCampaignById(String templateID);
}