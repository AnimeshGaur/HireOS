package com.conns.marketing.campaign.template.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.FilterEntity;

@Repository
public interface FilterRepository extends CrudRepository<FilterEntity, Long> {

	 @Override
	 List<FilterEntity> findAll();
}
