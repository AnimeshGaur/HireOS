package com.conns.marketing.campaign.template.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.StatesEntity;

@Repository
public interface StateEntityRepository extends CrudRepository<StatesEntity, Long>{



}
