package com.conns.marketing.campaign.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conns.marketing.campaign.exception.BadRequestException;

public class MandatoryFieldChecker {
	private static final Logger logger = LoggerFactory.getLogger(MandatoryFieldChecker.class);
	
	public static Boolean isNull(Object object) throws Exception {
		if(object == null) {
			logger.error("MandatoryFieldChecker.isNull - BadRequest - Object passed to is NULL. \n");
			throw new BadRequestException();
		}
		return true;
	}
}
