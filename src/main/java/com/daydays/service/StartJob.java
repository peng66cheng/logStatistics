package com.daydays.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.daydays.dao.LogDaoImpl;
import com.daydays.dao.OriginalLogDaoImpl;
import com.daydays.domain.LogItem;

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
	private String dateStr = "2016-10-32";
//	private String logFilePath = "/Users/bql/http/" + dateStr + '/';
	private String logFilePath = "/home/dpc/文档/http/" + dateStr + '/';
	

	private static final Logger logger = Logger.getLogger(StartJob.class);

	public void start() throws IOException {
		teacherClientStatistics();
		userClientStatistics();
		cmClientStatistics();
		operatorClientStatistics();
	}

	private void operatorClientStatistics() throws IOException {
		String fileFullName1 = logFilePath + "operator_client_http-monitor_90.log";
		String tableName = getTableName(fileFullName1);
		// 创建日志表
		logDao.createLogtable(tableName);
		originalLogDao.createLogtable(getOrignalLogTableName(fileFullName1));
		// 处理文件1
		dealLogFile(fileFullName1, tableName);

		logStatisticService.reportFile(tableName, "operator-client", dateStr);
	}

	private void cmClientStatistics() throws IOException {
		String fileFullName1 = logFilePath + "cm_client_http-monitor_36.log";
		String tableName = getTableName(fileFullName1);
		// 创建日志表
		logDao.createLogtable(tableName);
		originalLogDao.createLogtable(getOrignalLogTableName(fileFullName1));
		// 处理文件1
		dealLogFile(fileFullName1, tableName);

		// 处理文件2
		String fileFullName2 = logFilePath + "cm_client_http-monitor_37.log";
		dealLogFile(fileFullName2, tableName);

		// 处理文件3
		String fileFullName3 = logFilePath + "cm_client_http-monitor_129.log";
		dealLogFile(fileFullName3, tableName);
		// 处理文件4
		String fileFullName4 = logFilePath + "cm_client_http-monitor_130.log";
		dealLogFile(fileFullName4, tableName);

		logStatisticService.reportFile(tableName, "cm-client", dateStr);
	}

	private void userClientStatistics() throws IOException {
		String fileFullName1 = logFilePath + "user_client_http-monitor_86.log";
		String tableName = getTableName(fileFullName1);
		// 创建日志表
		logDao.createLogtable(tableName);
		originalLogDao.createLogtable(getOrignalLogTableName(fileFullName1));
		// 处理文件1
		dealLogFile(fileFullName1, tableName);

		String fileFullName2 = logFilePath + "user_client_http-monitor_87.log";
		// 处理文件2
		dealLogFile(fileFullName2, tableName);

		logStatisticService.reportFile(tableName, "user-client", dateStr);
	}

	private void teacherClientStatistics() throws IOException {
		String fileFullName1 = logFilePath + "teacher_client_http-monitor_83.log";
		String tableName = getTableName(fileFullName1);
		// 创建日志表
		logDao.createLogtable(tableName);
		originalLogDao.createLogtable(getOrignalLogTableName(fileFullName1));
		// 处理文件1
		dealLogFile(fileFullName1, tableName);

		String fileFullName2 = logFilePath + "teacher_client_http-monitor_84.log";
		// 处理文件2
		dealLogFile(fileFullName2, tableName);

		logStatisticService.reportFile(tableName, "teacher-client", dateStr);
	}

	private void dealLogFile(String fileFullName, String tableName) {
		List<List<LogItem>> result = getLogItems(fileFullName);
		if (CollectionUtils.isEmpty(result)) {
			return;
		}
		for (List<LogItem> logItems : result) {
			add2DB(logItems, tableName);
		}
	}

	private List<List<LogItem>> getLogItems(String fileFullName) {
		List<List<LogItem>> result = new ArrayList<>();
		List<LogItem> logItems = fileParse.parseFile(fileFullName, getOrignalLogTableName(fileFullName));
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

	private String getOrignalLogTableName(String fileFullName) {
		return getTableName(fileFullName) + "_org";
	}

	private String getTableName(String fileFullName) {
		String fileName = getFileName(fileFullName);
		String projectName = getProjectName(fileName);
		return LOG_TABLE_PRE + projectName + dateStr.replace('-', '_');
	}

	private String getFileName(String fileFullName) {
		int fileNameIndex = fileFullName.lastIndexOf('/');
		return fileFullName.substring(fileNameIndex + 1);
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
