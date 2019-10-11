package com.conns.marketing.campaign.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KillFile {
	private Boolean status;
	private FilterFile suppressionOut;
	private FilterFile suppressionIn;
	private ErrorOut suppressionError;

}
