package com.conns.marketing.campaign.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.marketing.campaign.model.Filterdata;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MMCS_Filters")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class FilterEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SEQ_ID")
	private Integer seqId;

	@Column(name = "FILTER_ID")
	private String filterId;

	@Column(name = "FILTER__DESCRIPTION")
	private String filterDesc;

	@Column(name = "FILTER_TYPE")
	private String filterType;

	@Type(type = "json")
	@Column(name = "FILTERS", columnDefinition = "json")
	List<Filterdata> filter;
}
