package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.conns.organization.management.entity.LocationMasterEntity;
import com.conns.organization.management.model.Location;

public interface LocationRepository extends CrudRepository<LocationMasterEntity, Long> {

	List<LocationMasterEntity> findAll();

	@Query("from LocationMasterEntity")
	List<LocationMasterEntity> findAll(Pageable pageRequest);

	boolean existsBylocationGUID(String locationGUID);

	LocationMasterEntity findByLocationGUID(String locationGUID);

	@Query(value = "call getLocationDetailsForDate(?1)", nativeQuery = true)
	List<Object[]> findByLocationDetailsForDate(String date);

	@Query(value = "From LocationMasterEntity Where status = ?1")
	List<Location> findByUdpatedStatus(String uiStatusUpdated);

}
