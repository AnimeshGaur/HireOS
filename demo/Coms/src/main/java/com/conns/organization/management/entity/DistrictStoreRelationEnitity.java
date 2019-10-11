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
@Table(name = "CONNS_DISTRICT_STORE_RELATION")
public class DistrictStoreRelationEnitity implements Serializable {

	private static final long serialVersionUID = 4987690521382066311L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SEQ_NO")
	private Integer seqNo;

	@Column(name = "DISTRICT_ID")
	private Integer districtId;

	@Column(name = "STORE_ID")
	private Integer storeId;

	@Column(name = "EFFECTIVE_START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveStartDate;

	@Column(name = "EFFECTIVE_END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveEndDate;

	@Column(name = "LAST_UPDATED_DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdatedDateTime;

}
