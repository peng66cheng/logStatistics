package com.daydays.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.daydays.DateUtil;
import com.daydays.dao.LogDaoImpl;
import com.daydays.dao.OriginalLogDaoImpl;
import com.daydays.domain.LogItem;
import com.daydays.utils.FileUtils;

@Service
public class StartJob {

	@Autowired
	private FileParseService fileParse;
	@Autowired
	private LogDaoImpl logDao;
	@Autowired
	private OriginalLogDaoImpl originalLogDao;
	@Autowired
	private LogStatisticsService logStatisticService;

	private final String LOG_TABLE_PRE = "http_log_";
	private String logFilePath = "/Users/bql/http/$date/";

	private static final Logger logger = Logger.getLogger(StartJob.class);

	public void start() throws IOException {
		//设置 处理日志日期
		String dateStr = DateUtil.getYesterday();
//		String dateStr = "2016-11-06";
		logFilePath = logFilePath.replace("$date", dateStr);
		//设置 处理日志日期 结束
		
		Set<String> logTableSet = new HashSet<>();
		File[] logFileNames = FileUtils.listSubFile(logFilePath);
		for (File logFile : logFileNames) {
			if(!logFile.getName().endsWith(".log")){
				continue;
			}
			String fileName = logFile.getName();
			String projectName = getProjectName(fileName);
			String tableName = LOG_TABLE_PRE + projectName + dateStr.replace('-', '_');
			logTableSet.add(tableName);
			
			String orgTableName = tableName + "_org";
			// 创建日志表
			logDao.createLogtable(tableName);
			originalLogDao.createLogtable(orgTableName);
			// 处理日志文件
			dealLogFile(logFilePath + fileName, tableName, orgTableName);
		}
		
		
//		logTableSet.add("http_log_cm_client2016_11_02");
//		logTableSet.add("http_log_teacher_client2016_11_02");
//		logTableSet.add("http_log_user_client2016_11_02");
//		logTableSet.add("http_log_operator_client2016_11_02");
//		
		report(logTableSet, logFilePath, dateStr);
	}

	private void report(Set<String> logTableSet, String logFilePath, String dateStr) throws IOException {
		for (String logTableName : logTableSet) {
			String projectName = logTableName.substring(LOG_TABLE_PRE.length(), logTableName.indexOf("20"));
			String statisticFileName = logFilePath + dateStr + ".xlsx";
			XSSFWorkbook workBook = logStatisticService.getWorkBook(statisticFileName);
			
			logStatisticService.addData2Excel(statisticFileName, workBook, logTableName, projectName);
		}
	}

	private void dealLogFile(String fileFullName, String tableName, String orgLogTable) throws IOException {
		List<List<LogItem>> result = getLogItems(fileFullName, orgLogTable);
		if (CollectionUtils.isEmpty(result)) {
			return;
		}
		for (List<LogItem> logItems : result) {
			add2DB(logItems, tableName);
		}
	}

	private List<List<LogItem>> getLogItems(String fileFullName, String orgLogTable) throws IOException {
		List<List<LogItem>> result = new ArrayList<>();
		List<LogItem> logItems = fileParse.parseFile(fileFullName, orgLogTable);
		if (CollectionUtils.isEmpty(logItems)) {
			logger.warn("has.no.info.fileName=" + fileFullName);
			return null;
		}
		int size = logItems.size();
		for (int i = 0; i < size; i += 1000) {
			result.add(logItems.subList(i, size < i + 1000 ? size : i + 1000));
		}
		return result;
	}

	private String getProjectName(String fileName) {
		int projectNameIndex = fileName.lastIndexOf("_http-monitor");
		return fileName.substring(0, projectNameIndex);
	}

	private void add2DB(List<LogItem> logItems, String projectName) {
		if (!CollectionUtils.isEmpty(logItems)) {
			logDao.addLogItems(logItems, projectName);
		}
	}

}
