package com.pramati.webcrawler.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.downloader.CrawlerEmailDownloadThread;
import com.pramati.webcrawler.webcrawler.CrawlerParserThread;
import com.pramati.webcrawler.util.URLProcessRecord;
import com.pramati.webcrawler.util.WebCrawlerFilter;
import com.pramati.webcrawler.util.WebCrawlerParser;
import com.pramati.webcrawler.util.WebCrawlerProperties;

/**
 * @author bhuvneshwars
 * <br><b>This is main class, which is having entry point to start crawling process</b>
 */

public class WebCrawlerMain extends WebCrawlerParser {
	
	final static Logger logger = Logger.getLogger(WebCrawlerMain.class);
	static String rootUrl = WebCrawlerProperties.getString("WebCrawler.WEB_URL");
	public static Object obj = new Object();
	
	static String year = null;
	Document doc = null;
	boolean monthMailStatus = true;
	
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
		} catch (InterruptedException e) {
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
		
		URLProcessRecord.getUnusedUrl().add(rootUrl);
		processWebUrl(rootUrl);
	}
	
	/**
	 * This method to process url and get url document to process
	 * @param webUrl
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void processWebUrl(String webUrl) throws IOException, InterruptedException {
		
		try {
			doc = Jsoup.connect(webUrl).get();
			
			if(WebCrawlerFilter.isEmailPage(doc)) {
				Object[] status =  WebCrawlerFilter.isMailForGivenYear(doc, year);
				if(Boolean.parseBoolean(status[0]+"")) {
					CrawlerTaskCollector.getInstance().addTask(webUrl, status[1].toString());
				}
			}
			
			processDoc(doc, webUrl);
		} catch(IllegalArgumentException e) {
			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		} catch(HttpStatusException e) {
			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		} catch(UnsupportedMimeTypeException e) {
			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		} catch(MalformedURLException e) {
			logger.error(e.getMessage()+" Exception occured due to invalid URL.");
		} 
	}
	
	/**
	 * This method is responsible to parse document and start thread 
	 * for further parsing and second thread downloading mails
	 * @param doc
	 * @param webUrl
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void processDoc(Document doc, String webUrl) throws IOException, InterruptedException {
		Set<String> links = null;
		links = getLinksFromPageElements(parseForAnchors(doc), year);
		URLProcessRecord.addUsedUrl(webUrl);
		URLProcessRecord.addUnusedUrl(links);
		
		CrawlerParserThread linkParserThread = null;
		int i=1;
		for(String link : links) {
			Set<String> linkSet = new HashSet<String>();
			linkSet.add(link);
			linkParserThread = new CrawlerParserThread(linkSet, year, i*CrawlerConstants.LIMIT_PER_THREAD); i++;
			linkParserThread.start();
			linkParserThread.join();
		}
		
		Entry<String, Set<String>> entryObj = null;
		CrawlerEmailDownloadThread taskObj = null;
		
		Iterator<Entry<String, Set<String>>> urlMapItr = CrawlerTaskCollector.getInstance().getTaskMap().entrySet().iterator();
		System.out.println("Coming...");
		while(urlMapItr.hasNext()) {
			entryObj = urlMapItr.next();
			taskObj = new CrawlerEmailDownloadThread(entryObj.getValue(), entryObj.getKey());
			taskObj.start();
		}
	}
	
}
