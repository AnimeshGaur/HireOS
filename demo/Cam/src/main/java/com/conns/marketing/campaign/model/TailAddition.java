package com.conns.marketing.campaign.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;;

@JsonInclude(Include.NON_NULL)
@Setter
@Getter
public class TailAddition {
	private List<ExternalFile> additionalFiles;

}
