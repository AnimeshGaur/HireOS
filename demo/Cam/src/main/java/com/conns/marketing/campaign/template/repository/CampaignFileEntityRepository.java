package com.conns.marketing.campaign.template.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.CampaignFileEntity;

@Repository
public interface CampaignFileEntityRepository extends CrudRepository<CampaignFileEntity, Long> {

	@Query(value = "FROM CampaignFileEntity WHERE CAMPAIGN_ID = ?1 AND FILE_ID = ?2")
	List<CampaignFileEntity> findByCampaignIdAndFileId(String campaignId, String fileId);

}
