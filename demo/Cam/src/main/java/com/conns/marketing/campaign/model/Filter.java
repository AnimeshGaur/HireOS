package com.conns.marketing.campaign.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter {
	private String filterId;
	private String filterDesc;
	private String type;
	private Integer seqId;
	private String condition;
	private List<Object> values;
	private List<State> data;
	private FilterFile filterOut;
	private FilterFile filterIn;
	private ErrorOut filterError;
	private List<String> offset;

}
