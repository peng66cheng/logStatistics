package com.daydays.utils;

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
	
	/**
	 * 获取第二天 凌晨2点的时间
	 * @return
	 */
	public long getNext2OClock(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime().getTime();
	}

	public static void main(String[] args) {
		System.out.println(getYesterday());
	}
}
