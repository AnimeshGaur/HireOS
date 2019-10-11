package com.conns.marketing.campaign.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;;

@Getter
@Setter
@JsonInclude (Include.NON_NULL)
public class SuppressionFile {
	private String path;
	private String fileName;
	private FilterFile suppressionOut;
	private FilterFile suppressionIn;
	private ErrorOut suppressionError;

	
}
