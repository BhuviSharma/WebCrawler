package com.pramati.webcrawler.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class URLProcessRecord {

//	private static BlockingQueue<String> unusedUrls = new LinkedBlockingQueue<String>();
//	private static BlockingQueue<String> usedUrls = new LinkedBlockingQueue<String>();
	
	private static Set<String> unusedUrls = new HashSet<String>();
	private static Set<String> usedUrls = new HashSet<String>();
	
	public static void addUnusedUrl(Set<String> webUrl) {
		unusedUrls.addAll(webUrl);
	}
	
	public static void addUsedUrl(String webUrl) {
		usedUrls.add(webUrl);
	}
	
	public static Set<String> getUnusedUrl() {
		return unusedUrls;
	}
	
	public static Set<String> getUsedUrl() {
		return usedUrls;
	}
	
}
