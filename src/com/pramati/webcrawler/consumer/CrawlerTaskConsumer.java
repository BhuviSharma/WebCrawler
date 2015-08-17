package com.pramati.webcrawler.consumer;

import java.util.concurrent.BlockingQueue;

import com.pramati.webcrawler.abstraction.TaskPerformer;

public class CrawlerTaskConsumer implements Runnable {

	private BlockingQueue<String> usedLinks;
	private BlockingQueue<String> unUsedLinks;
	private String link;
	
	public CrawlerTaskConsumer(BlockingQueue<String> usedLinks, BlockingQueue<String> unUsedLinks) {
		this.usedLinks = usedLinks;
		this.unUsedLinks = unUsedLinks;
	}
	
	public void run() {
		while(true) {
			link = unUsedLinks.poll();
			if(link!=null && !usedLinks.contains(link)) {
				TaskPerformer.getInstance().performTask(link);
				synchronized(usedLinks){
					usedLinks.add(link);
				}
			}
		}
	}
}
