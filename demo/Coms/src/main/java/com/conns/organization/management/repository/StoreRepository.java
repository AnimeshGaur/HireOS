package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.conns.organization.management.entity.StoreEntity;


public interface StoreRepository extends CrudRepository<StoreEntity, Integer> {

	@Query(value = "FROM StoreEntity WHERE storeNbr = ?1 ")
	StoreEntity findByStoreNumber(Integer storeNbr);

	@Query(value = "SELECT  str.store_id, str.store_nbr, str.store_location_name_1, sdr.effective_end_date FROM conns_district_store_relation sdr LEFT JOIN conns_store str on str.store_id = sdr.store_id WHERE sdr.district_id = ?1 And date(sdr.effective_end_date) >= '2999-12-31'", nativeQuery = true)
	List<Object[]> findByDistrictId(Integer districtId);

	@Query(value = "CALL getClosedLocationForCOM", nativeQuery = true)
	List<Object[]> findByStoreCloseDate();
	
	@Override
	List<StoreEntity> findAll();

	@Query(value = "FROM StoreEntity WHERE storeNbr = ?1 ")
	StoreEntity findByStoreNbr(Integer storeNbr);

	@Query(value = "select quota_date, quota_amount from conns_store_quota where store_nbr = 88 and year(quota_date) >= year(CURDATE())-1", nativeQuery = true)
	List<Object[]> findStoreQuotaByStoreNbr(Integer storeNbr);

}
