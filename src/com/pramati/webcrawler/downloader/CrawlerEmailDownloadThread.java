package com.pramati.webcrawler.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.util.WebCrawlerProperties;

public class CrawlerEmailDownloadThread extends Thread {
	
	private Logger logger = Logger.getLogger(CrawlerEmailDownloadThread.class);
	
	private static File rootDir = null;
	private File targetFile = null;
	private FileOutputStream fileOpStreamObj = null;
	private Element link = null;
	
	private Set<String> webUrlSet = null;
	private String fileName = null;
	
	public CrawlerEmailDownloadThread(Set<String> webUrlSet, String fileName) {
		this.webUrlSet = webUrlSet;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try{
			logger.info("Downloading mails for YEAR_MONTH : "+fileName);
			
			rootDir = new File(CrawlerConstants.HOME_PATH + WebCrawlerProperties.getString("WebCrawler.ROOT_FOLDER_PATH"));
			if(!rootDir.exists())
				rootDir.mkdirs();
			
			targetFile = new File(rootDir, fileName);
			fileOpStreamObj = new FileOutputStream(targetFile,true);
			
			for(String mailLink : webUrlSet){
				link = Jsoup.connect(mailLink).get().select(CrawlerConstants.MSG_VIEW_ID_QUERY).first();
				
				fileOpStreamObj.write(getMailSeparator());
				fileOpStreamObj.write((link.select(CrawlerConstants.FROM_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE).getBytes());
				fileOpStreamObj.write((link.select(CrawlerConstants.SUBJ_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE).getBytes());
				fileOpStreamObj.write((link.select(CrawlerConstants.DATE_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE).getBytes());
				fileOpStreamObj.write((link.select(CrawlerConstants.CONT_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE).getBytes());
				fileOpStreamObj.write(getMailSeparator());
			}
			
			logger.info("Successfuly Downloaded mails for YEAR_MONTH : "+fileName);
			
		} catch(Exception e){
			logger.error(e.getMessage()+" Exception occurred while downloading mail.");
			e.printStackTrace();
		} finally {
			try {
				fileOpStreamObj.flush();
				fileOpStreamObj.close();
			} catch (IOException e) {
				logger.error(e.getMessage()+" Exception occurred while closing file outputstream object.");
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] getMailSeparator() {
		return (CrawlerConstants.MAIL_SEPARATOR+CrawlerConstants.NEW_LINE).getBytes();
	}

}
