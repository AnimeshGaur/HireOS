package com.conns.organization.management.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CONNS_DISTRICT")
public class DistrictEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DISTRICT_ID")
	private Integer districtId;

	@Column(name = "DISTRICT_NAME")
	private String districtName;

	@Column(name = "DISTRICT_MANAGER")
	private String districtManager;

	@Column(name = "DM_HIRE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dmHireDate;

	@Column(name = "DISTRICT_CODE_AS400")
	private String districtCodeAs400;
}
