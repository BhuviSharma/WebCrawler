package com.pramati.webcrawler.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class WebCrawlerProperties {
	private static final String BUNDLE_NAME = "WebCrowler";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private WebCrawlerProperties() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
