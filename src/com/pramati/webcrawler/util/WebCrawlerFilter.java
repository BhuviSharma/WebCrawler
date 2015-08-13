package com.pramati.webcrawler.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.pramati.webcrawler.util.ValidationUtility;

/**
 * @author bhuvneshwars
 */
public class WebCrawlerFilter {
	
    /**
     * This method to check whether current document contains email or not
     * @param doc
     * @return
     */
    public static boolean isEmailPage(Document doc) {
        String content = null;
        Pattern pattern = null;
        Matcher matcher = null;
        if (doc != null) {
            content = doc.text();
            pattern = Pattern.compile("[fF]rom");
            matcher = pattern.matcher(content);
            if (!matcher.find()) {
                return false;
            }

            pattern = Pattern.compile("[dD]ate");
            matcher = pattern.matcher(content);

            if (!matcher.find()) {
                return false;
            }

            pattern = Pattern.compile("[sS]ubject");
            matcher = pattern.matcher(content);

            if (!matcher.find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method to decide whether this mail is for given year not
     * @param doc
     * @param year
     * @return obj[] contains : flag to decide this email for given year not,
     * if true then for which month
     */
    public static Object[] isMailForGivenYear(Document doc, String year) {
        Elements elements = null;
        String mailDate = null;
        Object[] status = new Object[2];
        if (doc != null && !ValidationUtility.isEmptyStringValue(year)) {
            elements = doc.select("td");
            for (int i = 0; i < elements.size(); i++) {
                if ("Date".equalsIgnoreCase(elements.get(i).text())) {
                	mailDate = elements.get(++i).text();
                    if (mailDate.length()>16 && year.equalsIgnoreCase(mailDate.substring(12, 16))) {
                    	status[0]=true;
                    	status[1]=mailDate.substring(12, 16)+ValidationUtility.monthMap.get(mailDate.substring(8, 11));
                    	return status;
                    }
                }
            }
        }
        status[0] = false;
        return status;
    }
}