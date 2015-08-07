package com.pramati.webcrawler.webcrawler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.resources.WebCrawlerProperties;

/**
 * This thread class is responsible for downloading mails for month
 * @author bhuvneshwars
 */
public class WebCrawlerThread implements Runnable{
	
	final static Logger logger = Logger.getLogger(WebCrawlerThread.class);
	
	Set<String> mailLinkSet;
	String fileName;
	Element link;

	File rootFolder = null;
	File outputFile = null;
	FileOutputStream fop = null;
	
	WebCrawlerThread(Set<String> mailLinkSet, String fileName) {
		this.mailLinkSet = mailLinkSet;
		this.fileName = fileName;
	}
	
	
	@Override
	public void run() {
		try{
			logger.info("Downloading mails for YEAR_MONTH : "+fileName);
			
			rootFolder = new File(CrawlerConstants.HOME_PATH + WebCrawlerProperties.getString("WebCrawler.ROOT_FOLDER_PATH"));
			if(!rootFolder.exists())
				rootFolder.mkdirs();
			outputFile = new File(rootFolder, fileName);
			fop = new FileOutputStream(outputFile,true);
			
			for(String mailLink : mailLinkSet){
				link = Jsoup.connect(mailLink).get().select(CrawlerConstants.MSG_VIEW_ID_QUERY).first();
				
				fop.write(getMailSeparator());
				fop.write((link.select(CrawlerConstants.FROM_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE).getBytes());
				fop.write((link.select(CrawlerConstants.SUBJ_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE).getBytes());
				fop.write((link.select(CrawlerConstants.DATE_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE).getBytes());
				fop.write((link.select(CrawlerConstants.CONT_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE).getBytes());
				fop.write(getMailSeparator());
			}
			
			logger.info("Successfuly Downloaded mails for YEAR_MONTH : "+fileName);
			
		} catch(Exception e){
			logger.error(e.getMessage()+" Exception occurred while downloading mail.");
			e.printStackTrace();
		} finally {
			try {
				fop.flush();
				fop.close();
			} catch (IOException e) {
				logger.error(e.getMessage()+" Exception occurred while closing file outputstream object.");
				e.printStackTrace();
			}
		}
	}
	
	
	public byte[] getMailSeparator() {
		return (CrawlerConstants.MAIL_SEPARATOR+CrawlerConstants.NEW_LINE).getBytes();
	}
}
