package com.conns.organization.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CONNS_REGION")
public class RegionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REGION_ID")
	private Integer regionId;

	@Column(name = "REGION_NAME")
	private String regionName;

	@Column(name = "RVP_FIRST_NAME")
	private String rvpFirstName;

	@Column(name = "RVP_LAST_NAME")
	private String rvpLastName;

}
