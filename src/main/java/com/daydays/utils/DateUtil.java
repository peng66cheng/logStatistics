package com.daydays;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

	/**
	 * 格式化时间为:年-月 SimpleDateFormat 有线程同步问题
	 */
	private final static ThreadLocal<SimpleDateFormat> sdfYMD = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	public static String getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return sdfYMD.get().format(cal.getTime());
	}

	public static void main(String[] args) {
		System.out.println(getYesterday());
	}
}
