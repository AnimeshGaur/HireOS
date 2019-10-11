package com.conns.marketing.campaign.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;;

@Getter
@Setter
@JsonInclude (Include.NON_NULL)
public class Source {
	private MasterData masterData;
	private List<ExternalFile> additionalFiles;
	private List<CampaignOutput> sourceCampaignOutputs;

	
}
