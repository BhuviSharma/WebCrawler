package com.pramati.webcrawler.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.webcrawler.constants.CrawlerConstants;


/**
 * This abstract class having common methods related to parsing of webpage
 * @author bhuvneshwars
 */
public abstract class WebCrawlerParser {

	/**
	 * This method to get Elements of WebUrl page based on css filter queries
	 * @param webLink, selectFilterQuery
	 * @return elements 
	 * @throws IOException
	 */
	public Elements getElementsInPage(String webLink, String... selectFilterQuery) throws IOException {
		Elements elements = Jsoup.connect(webLink).get().getAllElements();
		
		for(String query : selectFilterQuery)
			elements = elements.select(query);
		
		return elements;
	}
	
	/**
	 * This method is to get all sub element in Page Elements based on css filter queries
	 * @param elements, linkFilterCond
	 * @return mailEelementsList
	 * @throws IOException
	 */
	public List<Elements> getSubElements(Elements elements, String... linkFilterCond ) throws IOException {
		List<Elements> mailEelementsList = new ArrayList<Elements>();
		for(Element link: elements){
			mailEelementsList.add(getElementsInPage(link.absUrl(CrawlerConstants.LINK_TAG), linkFilterCond));
		}
		return mailEelementsList;
	}
	
	/**
	 * This method will return list of links which are exist in that Elements
	 * @param elements
	 * @return mailLinkList
	 */
	public Set<String> getLinksFromPageElements(Elements elements, String linkFilterCond) throws IOException {
		Set<String> mailLinkSet = new HashSet<String>();
		for(Element link: elements){
			if(linkFilterCond == null || link.attr(CrawlerConstants.LINK_TAG).contains(linkFilterCond))
				mailLinkSet.add(link.absUrl(CrawlerConstants.LINK_TAG).trim());
		}
		return mailLinkSet;
	}
}
