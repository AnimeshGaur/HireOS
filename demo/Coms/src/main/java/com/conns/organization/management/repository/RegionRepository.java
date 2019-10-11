package com.conns.organization.management.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.organization.management.entity.RegionEntity;

@Repository
public interface RegionRepository extends CrudRepository<RegionEntity, Long> {

	@Query(value = "FROM RegionEntity WHERE regionId = ?1")
	RegionEntity findbyRegionId(Integer regionId);
	

}
