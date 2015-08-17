package com.pramati.webcrawler.abstraction;

import org.jsoup.nodes.Document;

public interface CrawlerTask {
	
	void performTask(String url, String year);
	
	boolean isTaskTypeMatching(Document document);
	
}
