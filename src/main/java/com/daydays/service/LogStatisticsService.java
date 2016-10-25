package com.daydays.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daydays.dao.LogDaoImpl;
import com.daydays.domain.LogLevel;
import com.daydays.domain.UrlNum;
import com.daydays.domain.UrlRequestInfo;
import com.daydays.utils.FileUtils;

@Service
public class LogStatisticsService {

	private static final Logger logger = Logger.getLogger(LogStatisticsService.class);

	@Autowired
	private LogDaoImpl logDao;

	// 文件名称+大小+日期+项目名+机器
	// String getLogFileInfo(String projectName){
	// }
	//


	public void reportFile(String reportFilePath ,String tableName, String projectName, String statisticDate) throws IOException {
		List<UrlRequestInfo> logInfos = this.getRequestInfos(tableName);
		String statisticFileName = reportFilePath + statisticDate + ".xlsx";
		XSSFWorkbook workBook = getWorkBook(statisticFileName);
		XSSFSheet sheet = workBook.createSheet(projectName);
		addExcelHeader(sheet);
		add2Excel(logInfos, sheet);
		writeFile2Disk(workBook, statisticFileName);
	}

	private void writeFile2Disk(XSSFWorkbook workBook, String statisticFileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(statisticFileName);
		workBook.write(fos);
		fos.flush();
		fos.close();
	}

	public File getExcelFile(String statisticFileName) throws IOException {
		File file = new File(statisticFileName);
		if (!new File(statisticFileName).exists()) {
			// 需要先创建目录
			FileUtils.checkDir(statisticFileName.substring(0, statisticFileName.lastIndexOf("/")), true);
			FileUtils.createFile(statisticFileName);
			XSSFWorkbook workBook = new XSSFWorkbook();
			FileOutputStream fos = new FileOutputStream(file);
			workBook.write(fos);
			fos.flush();
			fos.close();
		}
		return file;
	}

	public XSSFWorkbook getWorkBook(String statisticFileName) throws IOException {
		File excelFile = getExcelFile(statisticFileName);
		FileInputStream fis = new FileInputStream(excelFile);
		XSSFWorkbook workBook = new XSSFWorkbook(fis);
		return workBook;
	}

	public XSSFSheet getXssfSheet(String statisticFileName, String projectName) throws IOException {
		XSSFWorkbook workBook = getWorkBook(statisticFileName);
		return workBook.createSheet(projectName);

	}

	public int add2Excel(List<UrlRequestInfo> urlInfos, XSSFSheet xssSheet) {
		int i = 0;
		for (UrlRequestInfo reqInfo : urlInfos) {
			XSSFRow row = xssSheet.createRow(++i);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(reqInfo.getUrl());
			row.createCell(cellIndex++).setCellValue(reqInfo.getTotal());
			row.createCell(cellIndex++).setCellValue(reqInfo.getWarnNum());
			row.createCell(cellIndex++).setCellValue(reqInfo.getErrorNum());
			row.createCell(cellIndex++).setCellValue(reqInfo.getWarnRatio());
		}
		return i;
	}

	private void addExcelHeader(XSSFSheet xssSheet) {
		XSSFRow row = xssSheet.createRow(0);
		int cellIndex = 0;
		row.createCell(cellIndex++).setCellValue("URL");
		row.createCell(cellIndex++).setCellValue("请求总数");
		row.createCell(cellIndex++).setCellValue(">600ms请求数");
		row.createCell(cellIndex++).setCellValue(">2000ms请求数");
		row.createCell(cellIndex++).setCellValue(">600ms请求所占比例");
	}

	public List<UrlRequestInfo> getRequestInfos(String tableName) {

		List<UrlRequestInfo> urlReqs = new ArrayList<>();

		List<UrlNum> urlsNum = logDao.queryUrlRequestNum(null, tableName);

		List<UrlNum> warnUrlsNum = logDao.queryUrlRequestNum(LogLevel.WARN.getId(), tableName);
		Map<String, Integer> warnUrlMap = UrlNum2Map(warnUrlsNum);

		List<UrlNum> errorUrlsNum = logDao.queryUrlRequestNum(LogLevel.ERROR.getId(), tableName);
		Map<String, Integer> errorUrlMap = UrlNum2Map(errorUrlsNum);

		for (UrlNum urlNum : urlsNum) {
			UrlRequestInfo urlReqInfo = new UrlRequestInfo();
			urlReqInfo.setUrl(urlNum.getUrl());
			urlReqInfo.setTotal(urlNum.getNum());
			if (warnUrlMap.containsKey(urlNum.getUrl())) {
				urlReqInfo.setWarnNum(warnUrlMap.get(urlNum.getUrl()));
			}
			if (errorUrlMap.containsKey(urlNum.getUrl())) {
				urlReqInfo.setErrorNum(errorUrlMap.get(urlNum.getUrl()));
			}
			urlReqInfo.setWarnRatio((urlReqInfo.getWarnNum() + urlReqInfo.getErrorNum()) * 100 / urlReqInfo.getTotal());

			urlReqs.add(urlReqInfo);
		}
		Collections.sort(urlReqs);
		return urlReqs;
	}

	private Map<String, Integer> UrlNum2Map(List<UrlNum> urlsNum) {
		Map<String, Integer> warnUrlNumMap = new HashMap<>();
		for (UrlNum warnUrlNum : urlsNum) {
			warnUrlNumMap.put(warnUrlNum.getUrl(), warnUrlNum.getNum());
		}
		return warnUrlNumMap;
	}

	// List<UrlRequestInfo> getRequestInfoByTime(){
	//
	// }

}
