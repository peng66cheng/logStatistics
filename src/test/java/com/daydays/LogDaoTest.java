package com.daydays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.daydays.dao.LogDaoImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext-config.xml" })
public class LogDaoTest {
	@Autowired
	private LogDaoImpl logDao;
	
//	@Ignore
	@Test
	public void test_createLogtable(){
		logDao.createLogtable("teacher_client");
	}
	
}
