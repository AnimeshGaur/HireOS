package com.conns.marketing.campaign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SearchResponse {

	@JsonProperty("Items")
	private Object items;

}
