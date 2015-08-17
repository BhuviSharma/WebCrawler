package com.pramati.webcrawler.producer;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.pramati.webcrawler.util.WebCrawlerParser;

public class CrawlerTaskProducer implements Runnable{
	
//	private Logger logger = Logger.getLogger(CrawlerTaskProducer.class);

	private BlockingQueue<String> usedLinks;
	private BlockingQueue<String> unUsedLinks;
	private String link;
	
	public CrawlerTaskProducer(BlockingQueue<String> usedLinks, BlockingQueue<String> unUsedLinks) {
		this.usedLinks = usedLinks;
		this.unUsedLinks = unUsedLinks;
	}
	
	@Override
	public void run() {
		while(true) {
			synchronized(unUsedLinks) {
				link = unUsedLinks.peek();
				if(!usedLinks.contains(link))
					produceTask(link);
			}
		}
			
	}
	
	/**
	 * This method to process link url and add all link urls from passing link web page to producer 
	 * @param linkUrl
	 */
	public void produceTask(String linkUrl) {
		Set<String> links = null;
		Document doc;
		try {
			doc = Jsoup.connect(linkUrl).get();
			links = WebCrawlerParser.getLinksFromPageElements(WebCrawlerParser.parseForAnchors(doc), null);
			
			unUsedLinks.addAll(links);
			
		} catch(IllegalArgumentException e) {
//			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		} catch(IOException e) {
//			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		}
	}
	
}
