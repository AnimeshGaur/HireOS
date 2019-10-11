package com.conns.marketing.campaign.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class State {
	private String stateName;
	private String addedZipCodes;
	private String removedZipCodes;
	private String stateCode;
	private Boolean active;

}
