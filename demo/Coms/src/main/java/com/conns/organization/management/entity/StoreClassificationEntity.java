package com.conns.organization.management.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CONNS_STORE_CLASSIFICATION")
public class StoreClassificationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SEQ_NO")
	private Integer seqNo;

	@Column(name = "STORE_NBR")
	private Integer storeNbr;

	@Column(name = "STORE_CLASSIFICATION")
	private String storeClassification;

	@Column(name = "EFFECTIVE_START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveStartDate;

	@Column(name = "EFFECTIVE_END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveEndDate;

}
