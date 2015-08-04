package com.pramati.webcrawler.webcrawler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.resources.WebCrawlerProperties;

/**
 * @author bhuvneshwars
 * <br>This is main class, which having entry point to start crawling process
 */

public class WebCrawlerMain {
	
	final static Logger logger = Logger.getLogger(WebCrawlerMain.class);
	
	static String year = null;
	Document doc = null;
	Elements elements = null;
	Set<String> yearMailingArchiveList = null;
	
	/**
	 * This is entry point to class 
	 * @param a
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
				logger.error("Given year is not valid.");
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
		
		doc = Jsoup.connect(WebCrawlerProperties.getString("WebCrawler.WEB_URL")).get();
		elements = doc.select(CrawlerConstants.TAG_CSS_QUERY);
		
		yearMailingArchiveList = new HashSet<String>();

		for(Element link: elements){
			if(link.attr(CrawlerConstants.LINK_TAG).contains(year)) {
				yearMailingArchiveList.add(link.absUrl(CrawlerConstants.LINK_TAG));
			}
		}
		if(yearMailingArchiveList.size()<=0) {
			logger.info("No links are found for this year.");
		}

		for(String mailingArchListUrl : yearMailingArchiveList) {
			processMailingArchListListDocument(mailingArchListUrl);
		}
	}
	
	/**
	 * This method to process all links for given year
	 * @param mailingArchListUrl
	 * @throws IOException
	 */
	void processMailingArchListListDocument(String mailingArchListUrl) throws IOException {
		logger.info("Processing Archive Mail : "+mailingArchListUrl);
		
		doc = Jsoup.connect(mailingArchListUrl).get();
		elements = doc.getAllElements().select(CrawlerConstants.MSG_LIST_ID_QUERY).
				select(CrawlerConstants.PAGE_NAV_CLASS_QUERY).select(CrawlerConstants.TAG_CSS_QUERY);
		
		Set<String> mailLinkSet = null;
		if(elements.size()>=0)
			mailLinkSet = getAllMailLinksList(elements, true);
		else {
			elements = doc.getAllElements().select(CrawlerConstants.MSG_LIST_ID_QUERY).
					select(CrawlerConstants.T_BODY).select(CrawlerConstants.TAG_CSS_QUERY);
			mailLinkSet = getAllMailLinksList(elements, false);
		}
		
		processMailLink(mailLinkSet, mailingArchListUrl);
	}
	
	/**
	 * This method to get all links based on the parameter 
	 * @param elements, isPageNumberLink
	 * @return mailLinkList 
	 * @throws IOException
	 */
	public Set<String> getAllMailLinksList(Elements elements, boolean isPageNumberLink) throws IOException{
		Set<String> mailLinkSet = null;
		List<Elements> subElementsList = null;
		
		if(isPageNumberLink) {
			mailLinkSet = new HashSet<String>();
			subElementsList = getAllSubElements(elements);
			
			for(Elements subElements : subElementsList) {
				mailLinkSet.addAll(getElementLinkList(subElements));
			}
		} else {
			mailLinkSet = getElementLinkList(elements);
		}
		return mailLinkSet;
	}
	
	/**
	 * This method is to get all sub elements in elements
	 * @param elements
	 * @return mailEelementsList
	 * @throws IOException
	 */
	public List<Elements> getAllSubElements(Elements elements) throws IOException {
		List<Elements> mailEelementsList = new ArrayList<Elements>();
		for(Element link: elements){
			mailEelementsList.add(Jsoup.connect(link.absUrl(CrawlerConstants.LINK_TAG)).get().
					select(CrawlerConstants.MSG_LIST_ID_QUERY).select(CrawlerConstants.T_BODY).select(CrawlerConstants.TAG_CSS_QUERY));
		}
		return mailEelementsList;
	}
	
	/**
	 * This method will return list of links which are exist in that Elements
	 * @param elements
	 * @return mailLinkList
	 */
	public Set<String> getElementLinkList(Elements elements) {
		Set<String> mailLinkSet = new HashSet<String>();
		for(Element link: elements){
				mailLinkSet.add(link.absUrl(CrawlerConstants.LINK_TAG));
		}
		return mailLinkSet;
	}
	
	/**
	 * This method is responsible to write mail contents to the file 
	 * @param mailLink
	 */
	public void processMailLink(Set<String> mailLinkSet, String parentUrl) {
//		Starting WebCrawler Thread to write file
		String fileName = parentUrl.substring(parentUrl.indexOf(year), 
				parentUrl.indexOf(year)+6);
		
		WebCrawlerThread wcThread = new WebCrawlerThread(mailLinkSet, fileName);
		wcThread.start();
	}
}
