package com.conns.organization.management.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.organization.management.entity.DistrictStoreRelationEnitity;

@Repository
public interface DistrictStoreRepository extends CrudRepository<DistrictStoreRelationEnitity, Integer> {

	@Query(value = "FROM DistrictStoreRelationEnitity WHERE storeId = ?1 ")
	DistrictStoreRelationEnitity findByStoreId(Integer storeId);
	
	@Query(value = "select * from conns_district_store_relation where store_id = ?1 and effective_end_date > now();",nativeQuery = true)
	DistrictStoreRelationEnitity findByStore(Integer storeId);

	
}
