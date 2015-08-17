package com.pramati.webcrawler.util;

import java.util.HashMap;

public class ValidationUtility {
	
	final static String HTTP = "http";
	final static String HTTPS = "https";
	
	public static HashMap<String, String> monthMap = new HashMap<String, String>();
	static {
		monthMap.put("Jan","01");
		monthMap.put("Feb","02");
		monthMap.put("Mar","03");
		monthMap.put("Apr","04");
		monthMap.put("May","05");
		monthMap.put("Jun","06");
		monthMap.put("Jul","07");
		monthMap.put("Aug","08");
		monthMap.put("Sep","09");
		monthMap.put("Oct","10");
		monthMap.put("Nov","11");
		monthMap.put("Dec","12");
	}

	public static boolean isEmptyStringValue(String value) {
		if(value == null || value.trim().equals(""))
			return true;
		return false;
	}
	
	public static boolean isVlaidUrl(String webUrl) {
		if(! isEmptyStringValue(webUrl) && (webUrl.startsWith(HTTP) || webUrl.startsWith(HTTPS)))
			return true;
		return false;
	}
	
	public static boolean isClass(String className) {
	    try  {
	        Class.forName(className);
	        return true;
	    }  catch (final ClassNotFoundException e) {
	        return false;
	    }
	}
}
