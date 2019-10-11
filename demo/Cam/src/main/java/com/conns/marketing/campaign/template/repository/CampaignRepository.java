package com.conns.marketing.campaign.template.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.CampaignEntity;
import com.conns.marketing.campaign.model.Campaign;

@Repository
public interface CampaignRepository extends CrudRepository<CampaignEntity, Long> {

	@Query(value = "FROM CampaignEntity WHERE SCHEDULED_DATE >= ?1")
	List<CampaignEntity> findByScheduledDateOutput(Date criteria);

	@Query(value = "FROM CampaignEntity WHERE scheduledDate BETWEEN  ?1 AND ?2")
	List<CampaignEntity> findByScheduledDateOutput(Date criteria, Date criteria1, Pageable pageable);
	
	@Query(value = "FROM CampaignEntity WHERE scheduledDate BETWEEN  ?1 AND ?2")
	List<CampaignEntity> findByScheduledDate(Date criteria, Date criteria1);

	@Query(value = "SELECT campaign FROM CampaignEntity WHERE statustracker = ?1 AND ?2<=scheduledDate AND scheduledDate>=?3  ")
	List<Campaign> findByStatus(String status, Date startTime, Date endTime);

	@Query(value = "SELECT campaign FROM CampaignEntity WHERE campaignId = ?1")
	Campaign findCampaignById(String campaignId);
	
	@Query(value = "FROM CampaignEntity WHERE campaignName=?1 AND ?2<=scheduledDate AND scheduledDate>=?3")
	List<CampaignEntity> findByName(String name,Date criteria, Date criteria1, Pageable pageable);
	
	CampaignEntity findByCampaignId(String campaignId);

	

	

	 

	

	
	

  
    
    

}
