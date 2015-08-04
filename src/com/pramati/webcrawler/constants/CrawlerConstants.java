package com.pramati.webcrawler.constants;


public interface CrawlerConstants {
	
	String HOME_PATH = System.getProperty("user.home");
	String MAIL_SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
	
//	Parsing related constants
	String TAG_CSS_QUERY = "a[href]";
	String LINK_TAG = "href";
	String T_BODY = "tbody";
	String MSG_LIST_ID_QUERY = "table[id=msglist]";
	String MSG_VIEW_ID_QUERY = "table[id=msgview]";
	String PAGE_NAV_CLASS_QUERY = "th[class=pages]";
	String FROM_CLASS_QUERY = "tr[class=from]";
	String SUBJ_CLASS_QUERY = "tr[class=subject]";
	String DATE_CLASS_QUERY = "tr[class=date]";
	String CONT_CLASS_QUERY = "tr[class=contents]";
	
	String NEW_LINE = "\n";
}
