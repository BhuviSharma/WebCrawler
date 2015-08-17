package com.pramati.webcrawler.crawlertask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.pramati.webcrawler.abstraction.CrawlerTask;
import com.pramati.webcrawler.constants.CrawlerConstants;
import com.pramati.webcrawler.util.ValidationUtility;
import com.pramati.webcrawler.resources.WebCrawlerProperties;


public class EmailDownloader implements CrawlerTask {
	
	private Logger logger = Logger.getLogger(EmailDownloader.class);

	Document document;

	String fileName;
	StringBuffer mailData;
	final File rootDir = new File(CrawlerConstants.HOME_PATH + WebCrawlerProperties.getString("WebCrawler.ROOT_FOLDER_PATH"));
	File targetFile;
	FileWriter targetFileWriter;
	
	public void performTask(final String url, final String year) {
		try {
			logger.info("Processing url : "+url);
			
			document = Jsoup.connect(url).get();
			if(document!=null && isTaskTypeMatching(document) && isMailForGivenYear(document, year)) {
				try {
					if(!rootDir.exists())
						rootDir.mkdirs();
				
					mailData = new StringBuffer();
					mailData.append(document.select(CrawlerConstants.FROM_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE);
					mailData.append(document.select(CrawlerConstants.SUBJ_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE);
					mailData.append(document.select(CrawlerConstants.DATE_CLASS_QUERY).text()+CrawlerConstants.NEW_LINE+CrawlerConstants.NEW_LINE);
					mailData.append(document.select(CrawlerConstants.CONT_CLASS_QUERY).text());
					
					fileName = document.select(CrawlerConstants.DATE_CLASS_QUERY).text();
					fileName = fileName.substring(17, 21)+ValidationUtility.monthMap.get(fileName.substring(13, 16))+"_"+document.select(CrawlerConstants.SUBJ_CLASS_QUERY).text();
					
					targetFile = new File(rootDir, fileName);
					targetFileWriter = new FileWriter(targetFile);
					
					targetFileWriter.write(mailData.toString());
				} catch (IOException e) {
					logger.error(e.getMessage()+" : Exception occurred during email downloading.");
				}  catch (IllegalArgumentException e) {
					logger.error(e.getMessage()+" : Exception occurred during email downloading.");
				} finally {
					targetFileWriter.flush();
					targetFileWriter.close();
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage()+" : Exception occurred during email downloading.");
		}
		 catch (IllegalArgumentException e) {
				logger.error(e.getMessage()+" : Exception occurred during email downloading.");
			}
	}
	
	@Override
	public boolean isTaskTypeMatching(Document document) {
		String content = null;
        Pattern pattern = null;
        Matcher matcher = null;
        boolean typeMatching = true;
        if (document != null) {
            content = document.text();
            for(String regex : CrawlerConstants.EMAIL_REGEX) {
            	pattern = Pattern.compile(regex);
                matcher = pattern.matcher(content);
                if (!matcher.find()) {
                	typeMatching = false;
                	break;
                }
            }
        }
        return typeMatching;
	}
	
	public boolean isMailForGivenYear(Document document, String year) {
		return document.select(CrawlerConstants.DATE_CLASS_QUERY).text().contains(year);
	}
	
}
