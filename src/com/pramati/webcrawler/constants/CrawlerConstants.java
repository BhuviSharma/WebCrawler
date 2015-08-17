package com.pramati.webcrawler.constants;


public interface CrawlerConstants {
	
	String CRAWLER_TASK_PACKAGE = "com.pramati.webcrawler.crawlertask.";
	
	String HOME_PATH = System.getProperty("user.home");
	
////	Parsing related constants
	String LINK_TAG = "href";
	
	String FROM_CLASS_QUERY = "tr[class=from]";
	String SUBJ_CLASS_QUERY = "tr[class=subject]";
	String DATE_CLASS_QUERY = "tr[class=date]";
	String CONT_CLASS_QUERY = "tr[class=contents]";
	
	String NEW_LINE = "\n";
	
	
//	EmailDownloader Constatns
	String EMAIL_REGEX[] = {"[fF]rom", "[dD]ate", "[sS]ubject"};
	
}
