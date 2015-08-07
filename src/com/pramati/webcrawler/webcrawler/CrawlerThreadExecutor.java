package com.pramati.webcrawler.webcrawler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pramati.webcrawler.constants.CrawlerConstants;


public class CrawlerThreadExecutor {

	private static ThreadPoolExecutor th = null;
	
	static {
		new CrawlerThreadExecutor().startThreadExecutor();
	}
	
	private void startThreadExecutor() {
		th = new ThreadPoolExecutor(CrawlerConstants.CORE_POOL_SIZE, CrawlerConstants.MAX_POOL_SIZE, 
				CrawlerConstants.KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public static void runTask(WebCrawlerThread threadObj) {
		th.execute(threadObj);
	}
}
