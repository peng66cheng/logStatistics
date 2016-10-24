package com.daydays;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.daydays.domain.UrlRequestInfo;
import com.daydays.service.LogStatisticsService;
import com.daydays.utils.FileUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext-config.xml" })
public class LogStatisticsServiceTest {

	private static final Logger logger = Logger.getLogger(LogStatisticsServiceTest.class);

	@Autowired
	private LogStatisticsService logStatisticsService;

	// @Ignore
	@Test
	public void test_getRequestInfos() throws IOException {
//		logStatisticsService.reportFile();
	}


}
