package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.conns.organization.management.entity.TreeNodeEntity;

public interface TreeRepository extends CrudRepository<TreeNodeEntity, Long> {

	@Query(value = "FROM TreeNodeEntity WHERE uiStatus = ?1")
	List<TreeNodeEntity> findByStatus(String status);

	@Query(value = "FROM TreeNodeEntity WHERE uiStatus = ?1 AND domain =?2")
	List<TreeNodeEntity> findByStatusAndDomain(String status, String domain);

	

}
