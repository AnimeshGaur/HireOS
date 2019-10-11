package com.conns.marketing.campaign.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;;

@JsonInclude (Include.NON_NULL)
@Getter
@Setter
public class Suppression {
	private KillFile employeeKillFile;
	private KillFile thirtyDayDelinquency; 
	private KillFile approvedDeclinedCustomerKillFile; 
	private KillFile connsKillFile;
	private KillFile storeKillFile;
	private KillFile exclusionList; 
	private KillFile bankruptcy;
	private KillFile salesMTD;
	private KillFile progressiveMTD;
	private List<SuppressionFile> suppressionFiles;
	private List<CampaignOutput> suppressionsCampaignOutputs;
	
	

}
