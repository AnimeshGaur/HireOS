package com.conns.marketing.campaign.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtil {

	public static String getCurrentUTCDate() { // handle exception
		DateTime today = new DateTime(DateTimeZone.UTC);
		Date date = today.toDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static String getDateFromJodaDate(DateTime dateTime) {
		Date date = dateTime.toDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static String getFormattedDate_yyyyMMdd(String dateTime) {
		DateTime dt = new DateTime(dateTime);
		Date date = dt.toDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date);
	}

	public static String getDateTimeAsCST(String dateTime) { // test
		String DATE_FORMAT = "yyyy-MM-dd";
		LocalDateTime ldt = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DATE_FORMAT));
		ZoneId chicagoZoneId = ZoneId.of("America/Chicago");
		ZonedDateTime chicagoZonedDateTime = ldt.atZone(chicagoZoneId);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);

		return format.format(chicagoZonedDateTime);
	}

	public static Boolean compareDates(String date1, String date2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt1 = sdf.parse(date1);
		Date dt2 = sdf.parse(date2);

		return dt1.before(dt2);
	}

	public static Date getFormattedDate(String date) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date effectivestartDate = df.parse(date);
		return effectivestartDate;
	}

}