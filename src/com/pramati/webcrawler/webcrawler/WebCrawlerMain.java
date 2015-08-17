package com.pramati.webcrawler.webcrawler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import com.pramati.webcrawler.abstraction.CrawlerTask;
import com.pramati.webcrawler.abstraction.TaskPerformer;
import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.consumer.CrawlerTaskConsumer;
import com.pramati.webcrawler.producer.CrawlerTaskProducer;
import com.pramati.webcrawler.util.ValidationUtility;
import com.pramati.webcrawler.resources.WebCrawlerProperties;

/**
 * @author bhuvneshwars
 * <br><b>This is main class, which is having entry point to start crawling process</b>
 */

public class WebCrawlerMain {
	
	final static Logger logger = Logger.getLogger(WebCrawlerMain.class);
	static String rootUrl = WebCrawlerProperties.getString("WebCrawler.WEB_URL");
	
	static String year = null;
	Document doc = null;
	boolean monthMailStatus = true;
	
	BlockingQueue<String> usedLinks = new LinkedBlockingQueue<String>();
	BlockingQueue<String> unUsedLinks = new LinkedBlockingQueue<String>();
	
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
				
				if(ValidationUtility.isClass(CrawlerConstants.CRAWLER_TASK_PACKAGE + 
						WebCrawlerProperties.getString("WebCrawler.CRAWLER_TASK_TYPE"))) {
					
					CrawlerTask taskObj = (CrawlerTask) Class.forName(CrawlerConstants.CRAWLER_TASK_PACKAGE + WebCrawlerProperties.getString("WebCrawler.CRAWLER_TASK_TYPE")).newInstance();
					TaskPerformer.getInstance().setTaskType(taskObj, year);
					
					webcrowlerObj = new WebCrawlerMain();
					webcrowlerObj.startWebcrowlingProcess();
				} else
					logger.error("This cralwer feature is not implemented yet. Please see configuration/Properties file for available features.");
			} else {
				logger.error("Given year "+year+" is not valid.");
			}
			
		} catch(IOException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
		} catch (InterruptedException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
		} catch (InstantiationException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage()+" Exception is occurred during Crawling Process.");
		}
	}
	
	/**
	 * This method is responsible to start actual crawling process
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	void startWebcrowlingProcess() throws IOException, InterruptedException {
		logger.info("Crawling Process is started for year "+year);
		
		unUsedLinks.add(rootUrl);
		Thread producerThread ;
		Thread consumerThread ;
		
		for(int i=0; i<10; i++) {
			producerThread = new Thread(new CrawlerTaskProducer(usedLinks, unUsedLinks), "Producer");
			consumerThread = new Thread(new CrawlerTaskConsumer(usedLinks, unUsedLinks), "Consumer");
			
			producerThread.start();
			consumerThread.start();
		}
	}
}
