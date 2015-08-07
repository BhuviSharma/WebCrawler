package com.pramati.webcrawler.webcrawler;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.parser.WebCrawlerParser;
import com.pramati.webcrawler.resources.WebCrawlerProperties;

/**
 * @author bhuvneshwars
 * <br><b>This is main class, which is having entry point to start crawling process</b>
 */

public class WebCrawlerMain extends WebCrawlerParser {
	
	final static Logger logger = Logger.getLogger(WebCrawlerMain.class);
	
	static String year = null;
	Document doc = null;
	Elements elements = null;
	Set<String> yearMailingArchiveSet = null;
	Map<String, Set<String>> monthMailMap = new HashMap<String, Set<String>>();
	
	/**
	 * This is entry point to class 
	 * @throws IOException
	 */
	public static void main(String[] a){
		try {
			logger.info("Requested WebCrawling Process");
			
			System.out.println("Enter Year to download mail : ");
			Scanner scannerObj = new Scanner(System.in);
			year = scannerObj.next();
			
			WebCrawlerMain webcrowlerObj = null; 
			if(year.matches("\\d+") && year.length()==4){
				webcrowlerObj = new WebCrawlerMain();
				webcrowlerObj.startWebcrowlingProcess();
			} else {
				logger.error("Given year "+year+" is not valid.");
			}
			
		} catch(IOException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is responsible to start actual crawling process
	 * @throws IOException
	 */
	void startWebcrowlingProcess() throws IOException {
		logger.info("Crawling Process is started for year "+year);
		
		elements = getElementsInPage(WebCrawlerProperties.getString("WebCrawler.WEB_URL"),	CrawlerConstants.LINK_CSS_QUERY);
		yearMailingArchiveSet = getLinksFromElements(elements, year, false);
		
		if(yearMailingArchiveSet.size()<=0) {
			logger.info("No links are found for this year.");
		}

		for(String mailingArchListUrl : yearMailingArchiveSet) {
			processMailingArchListListDocument(mailingArchListUrl);
		}
		
		processMailLink(monthMailMap);
	}
	
	/**
	 * This method to process all links for given year
	 * @param mailingArchListUrl
	 * @throws IOException
	 */
	void processMailingArchListListDocument(String mailingArchListUrl) throws IOException {
		
		logger.info("Processing Archive Mail : "+mailingArchListUrl);
		
		elements = getElementsInPage(mailingArchListUrl, CrawlerConstants.LINK_CSS_QUERY, 
				CrawlerConstants.PAGE_NAV_CLASS_QUERY, CrawlerConstants.LINK_CSS_QUERY);
		
		Set<String> mailLinkSet = null;
		if(elements.size()>0)
			mailLinkSet = getLinksFromElements(elements, null, true);
		
		else {
			elements = getElementsInPage(mailingArchListUrl, CrawlerConstants.MSG_LIST_ID_QUERY, 
					CrawlerConstants.T_BODY, CrawlerConstants.LINK_CSS_QUERY);
			mailLinkSet = getLinksFromElements(elements, null, false);
		}
		
		String fileName = mailingArchListUrl.substring(mailingArchListUrl.indexOf(year), 
				mailingArchListUrl.indexOf(year)+6);
		
		Set<String> monthArchiveMailLinks = monthMailMap.get(fileName)==null?new HashSet<String>():monthMailMap.get(fileName);
		
		monthArchiveMailLinks.addAll(mailLinkSet);
		monthMailMap.put(fileName, monthArchiveMailLinks);
		
	}
	
	
	
	/**
	 * This method to get all links based on the parameter 
	 * @param elements, isPageNumberLink
	 * @return mailLinkList 
	 * @throws IOException
	 */
	public Set<String> getLinksFromElements(Elements elements, String linkFilterCond, boolean isPageNumberLink) throws IOException{
		Set<String> mailLinkSet = null;
		List<Elements> subElementsList = null;
		
		if(isPageNumberLink) {
			mailLinkSet = new HashSet<String>();
			subElementsList = getSubElements(elements, CrawlerConstants.MSG_LIST_ID_QUERY, CrawlerConstants.T_BODY, 
					CrawlerConstants.LINK_CSS_QUERY);
			
			for(Elements subElements : subElementsList) {
				mailLinkSet.addAll(getLinksFromPageElements(subElements, linkFilterCond));
			}
		} else {
			mailLinkSet = getLinksFromPageElements(elements, linkFilterCond);
		}
		return mailLinkSet;
	}
	
	/**
	 * This method is responsible to write mail contents to the file 
	 * @param mailLink
	 */
	public void processMailLink(Map<String, Set<String>> monthMailMap) {
		Entry<String, Set<String>> toProcessSetOfMailLinks = null;
		WebCrawlerThread wcThread = null;
		
		Set<Entry<String, Set<String>>> linkMapEntrySet = monthMailMap.entrySet();
		
		Iterator<Entry<String, Set<String>>> it = linkMapEntrySet.iterator();
		while(it.hasNext()) {
			toProcessSetOfMailLinks = it.next();
			wcThread = new WebCrawlerThread(toProcessSetOfMailLinks.getValue(), toProcessSetOfMailLinks.getKey());
//			wcThread.start();
			CrawlerThreadExecutor.runTask(wcThread);
		}
	}
	
}
