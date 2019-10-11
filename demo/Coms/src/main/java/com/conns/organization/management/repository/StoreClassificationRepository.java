package com.conns.organization.management.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.organization.management.entity.StoreClassificationEntity;



@Repository
public interface StoreClassificationRepository extends CrudRepository<StoreClassificationEntity, Integer> {
	
	
	@Query(value = "select * from conns_store_classification where store_nbr = ?1 and year(effective_start_date) = ?2" , nativeQuery = true)
	StoreClassificationEntity findByStoreNbrAndDate(Integer storeNbr, String effective_start_date);


//	@Query(value = "select * from conns_store_classification str where store_nbr = ?1 and Year(str.effective_start_date) = ?2 and Year(str.effective_end_date) = ?2", nativeQuery = true)
//	StoreClassificationEntity findByStoreNbr(Integer storeNbr, String year);
	
	@Query(value = "FROM StoreClassificationEntity WHERE storeNbr = ?1 AND year(effectiveStartDate) = ?2 AND year(effectiveEndDate) = ?2")
	StoreClassificationEntity findByStoreNbr(Integer storeNbr, Integer year);
}
