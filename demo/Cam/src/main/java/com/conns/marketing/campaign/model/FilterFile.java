package com.conns.marketing.campaign.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterFile {
	private String fileId;
	private Boolean auditPassed;
	private String auditDate;
	private String recordCount;

}
