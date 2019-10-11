package com.conns.marketing.campaign.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.conns.marketing.campaign.model.State;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MMCS_STATES")
public class StatesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Type(type = "json")
	@Column(name = "STATES_ZIPCODES", columnDefinition = "json")
	List<State> statesZipCode;
}
