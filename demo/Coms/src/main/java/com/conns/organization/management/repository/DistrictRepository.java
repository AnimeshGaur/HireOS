package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.conns.organization.management.entity.DistrictEntity;

public interface DistrictRepository extends CrudRepository<DistrictEntity, Integer> {

	@Query(value = "select dst.district_id, dst.district_name from ConnsLocations_D.conns_district dst inner join ConnsLocations_D.conns_region_district_relation rgn on dst.district_id = rgn.district_id WHERE rgn.region_id = ?1 and rgn.effective_end_date = '3000-01-01'", nativeQuery = true)
	List<Object[]> findByRegionId(String regionId);

	@Query(value = "FROM DistrictEntity WHERE districtId = ?1 ")
	DistrictEntity findByDistrictId(Integer districtId);
	
	

}
