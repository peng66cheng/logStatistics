package com.daydays.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.daydays.domain.LogItem;
import com.daydays.utils.FileUtils;

@Service
public class FileParseService {

	private String URL_FLAG = "url=[";
	private String WARN_URL_FLAG = "invoke [";
	private String URL_END_FLAG = ".do";

	private String TIME_FLAG = "time=[";
	private String TIME_END_FLAG = "ms]";

	private String LOG_TIME_FLAG = "[20";
	private String LOG_TIME_END_FLAG = "] [catalina";

	private String DEBUG = "[DEBUG]";
	private String WARN = "[ WARN]";
	private String ERROR = "[ERROR]";

//	/passcode/noExistLoginNamePassCode; .html 请求未统计
	
	private static final Logger logger = Logger.getLogger(FileParseService.class);

	public List<LogItem> parseFile(String fileName) {

		try {
			
//			String tempFile;
			
//			FileUtils.readLineFromFile(fileName, new IExecutable() {
//				
//				@Override
//				public <String> void execute(Collection<String> collectoins) {
//					
//				}
//			});
			
			List<String> fileLines = FileUtils.readLineFromFile(fileName);
			if (CollectionUtils.isEmpty(fileLines)) {
				return null;
			}
			logger.info("fileName=" + fileName + ", line.number=" + fileLines.size());
			List<LogItem> logItems = new ArrayList<>();
			for (String fileLine : fileLines) {
				if (!fileLine.contains(URL_END_FLAG)) {
					logger.info("unDeal.line=" + fileLine);
					continue;
				}
				try {
					LogItem logItem = new LogItem();
					logItem.setLogLevel(getLogLevel(fileLine));
					if (logItem.getLogLevel() > 1) {
						logItem.setUrl(getUrl(fileLine, WARN_URL_FLAG));
					} else {
						logItem.setUrl(getUrl(fileLine, URL_FLAG));
					}
					logItem.setCostTime(getCostTime(fileLine));
					logItem.setLogTime(getlogTime(fileLine));
					logItems.add(logItem);
				} catch (Throwable t) {
					logger.error("parse.error:" + fileLine);
					continue;
				}
			}
			return logItems;

		} catch (IOException e) {
			return null;
		}
	}

	private int getLogLevel(String fileLine) {
		if (fileLine.startsWith(DEBUG)) {
			return 1;
		}
		if (fileLine.startsWith(WARN)) {
			return 2;
		}
		if (fileLine.startsWith(ERROR)) {
			return 3;
		}
		return -1;
	}

	private String getUrl(String fileLine, String urlStartFlag) {
		int urlBeginIndex = fileLine.indexOf(urlStartFlag);
		int urlEndIndex = fileLine.indexOf(URL_END_FLAG);
		if (urlBeginIndex < 0 || urlEndIndex < 0) {
			// fileLine
			return null;
		}
		return fileLine.substring(urlBeginIndex + urlStartFlag.length(), urlEndIndex + URL_END_FLAG.length());
	}

	private int getCostTime(String fileLine) {
		int timeBeginIndex = fileLine.indexOf(TIME_FLAG);
		int timeEndIndex = fileLine.indexOf(TIME_END_FLAG);
		if (timeBeginIndex < 0 || timeEndIndex < 0) {
			// fileLine
			return 0;
		}
		String cost = fileLine.substring(timeBeginIndex + TIME_FLAG.length(), timeEndIndex);
		return Integer.valueOf(cost);
	}

	private String getlogTime(String fileLine) {
		int logTimeBeginIndex = fileLine.indexOf(LOG_TIME_FLAG);
		int logTimeEndIndex = fileLine.indexOf(LOG_TIME_END_FLAG);
		if (logTimeBeginIndex < 0 || logTimeEndIndex < 0) {
			// fileLine
			return null;
		}
		return fileLine.substring(logTimeBeginIndex + 1, logTimeEndIndex);
	}
}
