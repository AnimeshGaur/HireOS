package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.organization.management.entity.OrganizationTreeEntity;

@Repository
public interface OrganizationRepository extends CrudRepository<OrganizationTreeEntity, Long> {

	@Query(value = "FROM OrganizationTreeEntity WHERE parentCommonId = ?1")
	List<OrganizationTreeEntity> findByParentCommonId(String commonId);


}
