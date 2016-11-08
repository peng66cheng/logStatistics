package com.daydays.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.daydays.dao.OriginalLogDaoImpl;
import com.daydays.domain.LogItem;
import com.daydays.utils.FileUtils;
import com.daydays.utils.IExecutable;

@Service
public class FileParseService {

	private String URL_FLAG = "url=[";
	private String WARN_URL_FLAG = "invoke [";
	private String URL_END_FLAG = "]";

	private String TIME_FLAG = "time=[";
	private String TIME_END_FLAG = "ms]";

	private String LOG_TIME_FLAG = "[20";
	private String LOG_TIME_END_FLAG = "] [catalina";

	private String DEBUG = "[DEBUG]";
	private String WARN = "[ WARN]";
	private String ERROR = "[ERROR]";

	// /passcode/noExistLoginNamePassCode; .html 请求未统计

	@Autowired
	private OriginalLogDaoImpl originalLogDao;

	private static final Logger logger = Logger.getLogger(FileParseService.class);

	public List<List<LogItem>> parseFile(String fileName, final String orgLogTableName) throws IOException {

		FileUtils.readLineFromFile(fileName, new IExecutable() {
			@Override
			public <String> void execute(Collection<String> orgLogs) {
				logger.info("日志文件入库：size=" + orgLogs.size());
				originalLogDao.addOriginalLog((Collection<java.lang.String>) orgLogs, orgLogTableName);
			}
		});

		int logNum = originalLogDao.queryOriginalLogSize(orgLogTableName);
		logger.info("fileName=" + fileName + ",logNum=" + logNum);
		List<List<LogItem>>  logItemsList = new ArrayList<>();
		int pageSize = 999;
		for (int startIndex = 0; startIndex < logNum;startIndex += pageSize) {
			List<String> orgLogs = originalLogDao.queryOriginalLog(orgLogTableName, startIndex, pageSize);
			if (CollectionUtils.isEmpty(orgLogs)) {
				break;
			}
			List<LogItem>  tempLogItems = getLogItems(orgLogs);
			
			logger.info("获取 日志项：tempLogItems.size=" + tempLogItems.size());
			logItemsList.add(tempLogItems);
		}
		return logItemsList;
	}

	private List<LogItem> getLogItems(List<String> fileLines) {
		List<LogItem> logItems = new ArrayList<>();
		for (String fileLine : fileLines) {
			if (!fileLine.contains(URL_FLAG) && !fileLine.contains(WARN_URL_FLAG)) {
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
		int urlEndIndex = fileLine.indexOf(URL_END_FLAG, urlBeginIndex);
		if (urlBeginIndex < 0 || urlEndIndex < 0) {
			// fileLine
			return null;
		}
		String url = fileLine.substring(urlBeginIndex + urlStartFlag.length(), urlEndIndex);
		return url.replaceAll("//", "/");
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
