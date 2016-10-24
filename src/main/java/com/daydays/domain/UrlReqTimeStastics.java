package com.daydays.domain;

/**
 * 请求 分时间段统计
 * @author dingpc
 *
 */
public class UrlReqTimeStastics {

	private String timeStr;
	private UrlRequestInfo urlRequestInfo;

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public UrlRequestInfo getUrlRequestInfo() {
		return urlRequestInfo;
	}

	public void setUrlRequestInfo(UrlRequestInfo urlRequestInfo) {
		this.urlRequestInfo = urlRequestInfo;
	}

}
