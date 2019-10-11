package com.conns.organization.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.conns.organization.management.model.Location;
import com.conns.organization.management.model.TreeNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TREE_NODE")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class TreeNodeEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "PARENT_COMMON_ID")
	private String parentCommonId;

	@Column(name = "DOMAIN")
	private String domain;

	@Column(name = "UI_STATUS")
	private String uiStatus;

	@Type(type = "json")
	@Column(name = "LOCATION", columnDefinition = "json")
	private Location location;

	@Type(type = "json")
	@Column(name = "TREE_NODE", columnDefinition = "json")
	private TreeNode treeNode;

}
