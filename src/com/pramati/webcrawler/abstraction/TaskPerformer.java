package com.pramati.webcrawler.abstraction;

public class TaskPerformer {
	
	private CrawlerTask taskObj;
	private String year;
	private static TaskPerformer taskPeformObj = null;
	
	private TaskPerformer() {
	}
	
	public static TaskPerformer getInstance(){
		if(taskPeformObj==null) {
			taskPeformObj = new TaskPerformer();
		}
		return taskPeformObj;
	}

	public void setTaskType(CrawlerTask taskObj, String year) {
		this.taskObj = taskObj;
		this.year = year;
	}
	
	public void performTask(String url) {
		this.taskObj.performTask(url, year);
	}
}
