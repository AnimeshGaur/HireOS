package com.conns.organization.management.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.organization.management.model.Person;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "EMPLOYEE")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class PersonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "EMPLOYEE_UNIQUE_ID")
	private String employeeUniqueId;

	@Column(name = "EMPLOYEE_ID")
	private String employeeId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "MIDDLE_NAME")
	private String middleName;

	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "STATUS")
	private String status;

	@Type(type = "json")
	@Column(name = "PERSON", columnDefinition = "json")
	private Person person;

}
