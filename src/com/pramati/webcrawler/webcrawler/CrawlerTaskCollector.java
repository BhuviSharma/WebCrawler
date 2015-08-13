package com.pramati.webcrawler.webcrawler;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class CrawlerTaskCollector {
	
	private static CrawlerTaskCollector taskCollObj = null;
	private static Hashtable<String, Set<String>> taskMap = new Hashtable<String, Set<String>>();

	private CrawlerTaskCollector() {
	}
	
	public static CrawlerTaskCollector getInstance() {
		if(taskCollObj==null)
			return new CrawlerTaskCollector();
		return taskCollObj;
	}
	
	public synchronized void addTask(String webUrl, String fileName) {
		Set<String> webUrlSet = taskMap.get(fileName) == null ? new HashSet<String>() : taskMap.get(fileName);
		webUrlSet.add(webUrl);
		taskMap.put(fileName, webUrlSet);
	}
	
	public Hashtable<String, Set<String>> getTaskMap() {
		return taskMap;
	}
	
}
