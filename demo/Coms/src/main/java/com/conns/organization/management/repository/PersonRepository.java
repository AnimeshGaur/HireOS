package com.conns.organization.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.conns.organization.management.entity.PersonEntity;
import com.conns.organization.management.model.Person;

public interface PersonRepository extends CrudRepository<PersonEntity, Long> {

	@Query(value = "FROM PersonEntity WHERE employeeUniqueId = ?1")
	List<PersonEntity> findByEmployeeUniqueIdId(String employeeUniqueId);

	@Query(value = "FROM PersonEntity WHERE status= ?1")
	List<Person> findByStatus(String uiStatusUpdated);

}
