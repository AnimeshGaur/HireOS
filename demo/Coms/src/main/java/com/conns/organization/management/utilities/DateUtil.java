package com.conns.organization.management.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtil {
	public static String getCurrentUTCDate() {
		DateTime today = new DateTime(DateTimeZone.UTC);
		Date date = today.toDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static Date getFormateddate(String date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
		return dateFormat.parse(date);
	}
	
	public static String getFormateddate(Date date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
		return dateFormat.format(date);
	}

}
