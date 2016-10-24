package com.daydays.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OriginalLogDaoImpl {
	@Resource
	private JdbcTemplate jdbcTemplate;

	private String createTableSql = "CREATE TABLE `original_log` (\n"
			+ "    id BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n"
			+ "    log_info VARCHAR (255) DEFAULT NULL COMMENT '日志详情',\n" + "    PRIMARY KEY (`id`)\n"
			+ ") ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = '源日志表';";
	
	
}
