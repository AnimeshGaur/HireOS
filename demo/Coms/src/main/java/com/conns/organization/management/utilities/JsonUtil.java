package com.conns.organization.management.utilities;

import java.util.ArrayList;
import java.util.List;

import com.conns.organization.management.model.LocationV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

	public static String toLocationString(List<LocationV2> locations) {
		List<String> locationList = new ArrayList<>();
		locations.forEach((location) -> {
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				locationList.add(mapper.writeValueAsString(location));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		return locationList.toString();
	}
	

}
