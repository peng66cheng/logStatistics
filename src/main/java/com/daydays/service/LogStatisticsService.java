package com.daydays.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	private static Set<String> cm_special_url = new HashSet<String>();
	static {
		cm_special_url.add("/schools");
		cm_special_url.add("/teachers");
		cm_special_url.add("/paper");
		cm_special_url.add("/papers");
		cm_special_url.add("/cc");
		cm_special_url.add("/ct");
		cm_special_url.add("/sc");
		cm_special_url.add("/st");
		cm_special_url.add("/exam");
	}
	private String CM_PRE = "cm";

	@Autowired
	private LogDaoImpl logDao;

	// 文件名称+大小+日期+项目名+机器
	// String getLogFileInfo(String projectName){
	// }
	//

	public void addData2Excel(String statisticFileName, XSSFWorkbook workBook, String tableName, String projectName)
			throws IOException {
		List<UrlRequestInfo> logInfos = this.getRequestInfos(tableName);

		if (!tableName.contains(CM_PRE)) {// cm-client 需特殊处理。
			add2Sheet(statisticFileName, workBook, projectName, logInfos);
			return;
		}

		Map<String, List<UrlRequestInfo>> reClassSheet = reClassSheet(logInfos);
		for (Entry<String, List<UrlRequestInfo>> urlRequestInfo : reClassSheet.entrySet()) {
			logInfos.removeAll(urlRequestInfo.getValue());// 移除
			add2Sheet(statisticFileName, workBook, urlRequestInfo.getKey(), urlRequestInfo.getValue());
		}
		add2Sheet(statisticFileName, workBook, projectName, logInfos);
	}

	/**
	 * 对于特殊url，重新分配 工作薄
	 * @param logInfos
	 * @return
	 */
	private Map<String, List<UrlRequestInfo>> reClassSheet(List<UrlRequestInfo> logInfos) {
		// key 为 excel sheet;
		Map<String, List<UrlRequestInfo>> urlRequests = new HashMap<>();
		for (int i = 0; i < logInfos.size(); i++) {
			UrlRequestInfo urlRequestInfo = logInfos.get(i);
			if (urlRequestInfo.getUrl().length() <= 1) {
				continue;
			}
			int secondDecIndex = urlRequestInfo.getUrl().indexOf("/", 1);
			if (secondDecIndex < 0) {
				continue;
			}
			String urlPre = urlRequestInfo.getUrl().substring(0, secondDecIndex);
			if (!cm_special_url.contains(urlPre)) {
				continue;
			}
			String excelsheetName = CM_PRE + "-" + urlPre.substring(1) + "请求";
			List<UrlRequestInfo> tempUrlList = urlRequests.get(excelsheetName);
			if (tempUrlList == null) {
				tempUrlList = new ArrayList<>();
				urlRequests.put(excelsheetName, tempUrlList);
			}
			tempUrlList.add(urlRequestInfo);
		}
		return urlRequests;
	}

	private void add2Sheet(String statisticFileName, XSSFWorkbook workBook, String sheetName, List<UrlRequestInfo> logInfos) throws IOException {
		logger.info("写入工作薄，sheetName="+sheetName);
		XSSFSheet sheet = workBook.createSheet(sheetName);
		addExcelHeader(sheet);
		logger.info("开始排序");
		Collections.sort(logInfos);// 先排序，在存excel
		logger.info("排序结束");
		add2Excel(logInfos, sheet);
		this.writeFile2Disk(workBook, statisticFileName);
		logger.info("写入工作薄，sheetName="+sheetName+"结束");
	}

	public void writeFile2Disk(XSSFWorkbook workBook, String statisticFileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(statisticFileName);
		workBook.write(fos);
		fos.flush();
		fos.close();
	}

	/**
	 * 创建excel文件
	 * @param statisticFileName
	 * @return
	 * @throws IOException
	 */
	public File getExcelFile(String statisticFileName) throws IOException {
		logger.info("创建文件:" + statisticFileName);
		File file = new File(statisticFileName);
		if (!new File(statisticFileName).exists()) {
			// 需要先创建目录
			FileUtils.checkDir(statisticFileName.substring(0, statisticFileName.lastIndexOf("/")), true);
			FileUtils.createFile(statisticFileName);
			FileOutputStream fos = null;
			try {
				XSSFWorkbook workBook = new XSSFWorkbook();
				fos = new FileOutputStream(file);
				workBook.write(fos);
				fos.flush();
			} catch (Exception t) {
				throw t;
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
		return file;
	}

	public XSSFWorkbook getWorkBook(String statisticFileName) throws IOException {
		File excelFile = getExcelFile(statisticFileName);
		FileInputStream fis = new FileInputStream(excelFile);
		XSSFWorkbook workBook = new XSSFWorkbook(fis);
		return workBook;
	}

//	public XSSFSheet getXssfSheet(String statisticFileName, String projectName) throws IOException {
//		XSSFWorkbook workBook = getWorkBook(statisticFileName);
//		return workBook.createSheet(projectName);
//
//	}

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

		logger.info("查询url数，开始");
		List<UrlNum> urlsNum = logDao.queryUrlRequestNum(tableName);
		logger.info("查询url数，结束");
		
		logger.info("查询warn url数，开始");
		List<UrlNum> warnUrlsNum = logDao.queryUrlRequestNum(LogLevel.WARN.getId(), tableName);
		Map<String, Integer> warnUrlMap = UrlNum2Map(warnUrlsNum);
		logger.info("查询warn url数，结束");
		
		logger.info("查询error url数，开始");
		List<UrlNum> errorUrlsNum = logDao.queryUrlRequestNum(LogLevel.ERROR.getId(), tableName);
		Map<String, Integer> errorUrlMap = UrlNum2Map(errorUrlsNum);
		logger.info("查询error url数，结束");
		
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
		logger.info("url 统计结束");
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
