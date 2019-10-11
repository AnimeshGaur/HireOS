package com.conns.marketing.campaign.template.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.conns.marketing.campaign.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

	List<CustomerEntity> findCustomerByIdIn(List<String> customerId);

}
