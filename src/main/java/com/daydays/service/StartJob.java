package com.daydays.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daydays.utils.DateUtil;
import com.daydays.utils.FileUtils;

@Service
public class StartJob {

	@Autowired
	private FileParseService fileParse;
	@Autowired
	private LogStatisticsService logStatisticService;

	private final String LOG_TABLE_PRE = "http_log_";
	private String logFilePath = "/Users/bql/http/$date/";
	
	private static final Logger logger = Logger.getLogger(StartJob.class);

	public void start() throws IOException {
		// 设置 处理日志日期
		String dateStr = DateUtil.getYesterday();
//		 String dateStr = "2016-12-01";
		logFilePath = logFilePath.replace("$date", dateStr);
		// 设置 处理日志日期 结束
		logger.info("日志文件路径为：" + logFilePath);
		File[] logFileNames = FileUtils.listSubFile(logFilePath);
		Map<String,String> fileTableNameMap = getLogTableName(logFileNames, dateStr);
		recordOrgLog(fileTableNameMap, logFileNames);
		Set<String> tablesName = new HashSet<>(fileTableNameMap.values());
		recordLogItems(tablesName);
		
		// logTableSet.add("http_log_cm_client2016_11_07");
		// logTableSet.add("http_log_teacher_client2016_11_07");
		// logTableSet.add("http_log_user_client2016_11_07");
		// logTableSet.add("http_log_operator_client2016_11_07");

		report(tablesName, logFilePath, dateStr);
	}

	private void recordLogItems(Collection<String> logTableSet) throws IOException {
		for (String logTableName : logTableSet) {
			fileParse.logItemsToDB(logTableName);
		}
	}
	
	private void recordOrgLog(Map<String,String>  tableNameMap, File[] logFileNames) throws IOException {
		for (File logFile : logFileNames) {
			if (!logFile.getName().endsWith(".log")) {
				continue;
			}
			String fileName = logFile.getName();
			logger.info("日志文件入库：" + fileName);
			String tableName = tableNameMap.get(logFile.getName());
			fileParse.recordOrgLog(logFilePath + fileName, tableName);
		}
	}
	
	/**
	 * 计算日志文件对应的 表
	 * 
	 * @param logFileNames
	 * @param dateStr
	 * @return  map<文件名，表名>
	 */
	private Map<String, String>  getLogTableName(File[] logFileNames,String dateStr){
		Map<String,String> map = new HashMap<>();
		for (File logFile : logFileNames) {
			if (!logFile.getName().endsWith(".log")) {
				continue;
			}
			String fileName = logFile.getName();
			String projectName = getProjectName(fileName);
			String tableName = LOG_TABLE_PRE + projectName + dateStr.replace('-', '_');
			map.put(logFile.getName(), tableName);
		}
		return map;
	}

	private void report(Collection<String> logTableSet, String logFilePath, String dateStr) throws IOException {
		for (String logTableName : logTableSet) {
			logger.info("统报告计：logTableName=" + logTableName);
			String projectName = logTableName.substring(LOG_TABLE_PRE.length(), logTableName.indexOf("20"));
			String statisticFileName = logFilePath + dateStr + ".xlsx";
			logStatisticService.addData2Excel(statisticFileName, logTableName, projectName);
			logger.info("统报告计：logTableName=" + logTableName + "结束");
		}
	}

	private String getProjectName(String fileName) {
		int projectNameIndex = fileName.lastIndexOf("_http-monitor");
		return fileName.substring(0, projectNameIndex);
	}

}
