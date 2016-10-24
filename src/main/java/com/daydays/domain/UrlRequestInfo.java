package com.daydays.domain;

/**
 * url 请求信息
 * 
 * @author dingpc
 *
 */
public class UrlRequestInfo implements Comparable<UrlRequestInfo> {

	private String url;

	private int total;

	private int warnNum;

	private int errorNum;

	private float warnRatio;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getWarnNum() {
		return warnNum;
	}

	public void setWarnNum(int warnNum) {
		this.warnNum = warnNum;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public float getWarnRatio() {
		return warnRatio;
	}

	public void setWarnRatio(float warnRatio) {
		this.warnRatio = warnRatio;
	}

	@Override
	public int compareTo(UrlRequestInfo o) {
		if(this == o){
			return 0;
		}
		if (o == null) {
			return 1;
		}
		if (this.warnRatio == o.getWarnRatio()) {
			return 0;
		}
		if (this.warnRatio < o.getWarnRatio()) {
			return 1;
		}
		return -1;
	}

}
