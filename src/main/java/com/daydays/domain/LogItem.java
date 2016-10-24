package com.daydays.domain;

/**
 * 日志项
 * 
 * @author dingpc
 *
 */
public class LogItem {
	
	private String url;
	
	private String logTime;
	
	private int costTime;
	
	private int logLevel;
	
	private String orignalStr;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public int getCostTime() {
		return costTime;
	}

	public void setCostTime(int costTime) {
		this.costTime = costTime;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public String getOrignalStr() {
		return orignalStr;
	}

	public void setOrignalStr(String orignalStr) {
		this.orignalStr = orignalStr;
	}

}
