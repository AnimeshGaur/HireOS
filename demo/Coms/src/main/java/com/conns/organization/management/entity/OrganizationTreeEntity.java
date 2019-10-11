package com.conns.organization.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.organization.management.model.OrganizationTree;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ORGANIZATION")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class OrganizationTreeEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "PARENT_COMMON_ID")
	private String parentCommonId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Type(type = "json")
	@Column(name = "ORGANIZATION_TREE",columnDefinition = "json")
	private OrganizationTree organizationTree;
}
