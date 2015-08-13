package com.pramati.webcrawler.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import com.pramati.webcrawler.util.URLProcessRecord;
import com.pramati.webcrawler.util.WebCrawlerFilter;
import com.pramati.webcrawler.util.WebCrawlerParser;
import com.pramati.webcrawler.webcrawler.CrawlerTaskCollector;

public class CrawlerParserThread extends Thread {

	private Logger logger = Logger.getLogger(CrawlerParserThread.class);

	private Set<String> linkSet = null;
	private String year = null;
	private Document doc = null;
	Set<String> links = null;
	String monthYearVal = null;
	int limitPerThread = 0;

	public CrawlerParserThread(Set<String> linkSet, String year, int limitPerThread) {
		this.linkSet = linkSet;
		this.year = year;
		this.limitPerThread = limitPerThread;
	}

	@Override
	public void run() {
		processWebUrl(linkSet);
	}

	public void processWebUrl(Set<String> urlSet){
		
		for(String webUrl : urlSet) {
			monthYearVal = webUrl.replaceAll("\\D+", "");
			if(!URLProcessRecord.getUsedUrl().contains(webUrl) && (monthYearVal==null || 
					monthYearVal.trim().equals("") || monthYearVal.contains(year))){
				
				try{
					doc = Jsoup.connect(webUrl).timeout(80000).get();
				
					if(WebCrawlerFilter.isEmailPage(doc)) {
						Object[] status =  WebCrawlerFilter.isMailForGivenYear(doc, year);
						if(Boolean.parseBoolean(status[0]+"")) {
							logger.info("Parsing URL "+webUrl);
							CrawlerTaskCollector.getInstance().addTask(webUrl, status[1].toString());
						}
					}
					URLProcessRecord.addUsedUrl(webUrl);
					processDoc(doc);
				} catch(IllegalArgumentException e) {
					URLProcessRecord.addUsedUrl(webUrl);
				} catch(HttpStatusException e) {
					URLProcessRecord.addUsedUrl(webUrl);
				} catch(UnsupportedMimeTypeException e) {
					URLProcessRecord.addUsedUrl(webUrl);
				} catch(MalformedURLException e) {
					URLProcessRecord.addUsedUrl(webUrl);
				} catch(IOException e) {
					URLProcessRecord.addUsedUrl(webUrl);
					logger.error(e.getMessage()+" Exception occured due to invalid URL.");
				}
			}
		}
	}
	
	public void processDoc(Document doc) {
		try {
			links = WebCrawlerParser.getLinksFromPageElements(WebCrawlerParser.parseForAnchors(doc), null);
		} catch(IOException e) {
			links = null;
		}
		if(links != null) URLProcessRecord.addUnusedUrl(links);
		
		if(limitPerThread > URLProcessRecord.getUnusedUrl().size()) {
			processWebUrl(links);
		} else {
			CrawlerParserThread parseThread = new CrawlerParserThread(links, year, limitPerThread);
			parseThread.start();
		}
	}

}
