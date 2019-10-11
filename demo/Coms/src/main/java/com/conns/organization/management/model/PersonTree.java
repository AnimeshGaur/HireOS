package com.conns.organization.management.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonTree {
	private String employeeUniqueId;
	private String employeeId;
	private String name;
	private String positionId;
	private String position;
	private Integer level;
	private String hrId;
	private String parentPositionId;


}
