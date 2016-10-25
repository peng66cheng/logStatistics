package com.daydays.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.daydays.domain.LogItem;
import com.daydays.domain.UrlNum;

@Component
public class LogDaoImpl {

	private static final Logger logger = Logger.getLogger(LogDaoImpl.class);

	@Resource
	private JdbcTemplate jdbcTemplate;

	private String createTableSql = "CREATE TABLE if not exists $tablename (\n"
			+ "	id BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n"
			+ "	url VARCHAR (255) DEFAULT NULL COMMENT '请求Url',\n"
			+ "	cost INT (11) DEFAULT NULL COMMENT '请求处理时间，单位为ms',\n"
			+ "	log_level INT (11) DEFAULT NULL COMMENT '日志级别(1 debug, 2 warn 3 error)',\n"
			+ "	log_time VARCHAR (255) DEFAULT NULL COMMENT '日志打印时间',\n"
			+ "	create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" + "	PRIMARY KEY (`id`)\n"
			+ ") ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = 'http日志表';";

	private String reqNumQuerySql = "SELECT\n" + "	url,\n" + "	COUNT(1) num\n" + "FROM\n" + "	$tablename\n"
			+ "WHERE\n" + "	1 = 1\n" + "$conditions" + "GROUP BY\n" + "	url;";

	public void createLogtable(String tableName) {
		try {
			String tempCreateSql = createTableSql.replace("$tablename", tableName);
			this.jdbcTemplate.execute(tempCreateSql);
		} catch (Exception e) {
			logger.warn("创建表失败，table.name=" + tableName);
			throw e;
		}
	}

	public int addLogItems(List<LogItem> logItems, String projectName) {
		if (CollectionUtils.isEmpty(logItems)) {
			return 0;
		}

		String sql = "insert into " + projectName + "(url, cost, log_level, log_time) values ";
		Iterator<LogItem> iterator = logItems.iterator();
		while (iterator.hasNext()) {
			LogItem logItem = iterator.next();
			sql += "('" + logItem.getUrl() + "'," + logItem.getCostTime() + "," + logItem.getLogLevel() + ",'"
					+ logItem.getLogTime() + "'),";
		}
		sql = sql.substring(0, sql.length() - 1);
		int result = this.jdbcTemplate.update(sql);
		this.jdbcTemplate.execute("commit;");
		return result;
	}

	public List<UrlNum> queryUrlRequestNum(Integer logLevel, String projectName) {
		String tempUrl = reqNumQuerySql.replace("$tablename", projectName);
		if (logLevel != null) {
			tempUrl = tempUrl.replace("$conditions", "AND log_level =" + logLevel + "\n");
		} else {
			tempUrl = tempUrl.replace("$conditions", "");
		}
		return this.jdbcTemplate.query(tempUrl, new RowMapper<UrlNum>() {

			@Override
			public UrlNum mapRow(ResultSet rs, int rowNum) throws SQLException {
				UrlNum urlNum = new UrlNum();
				urlNum.setUrl(rs.getString("url"));
				urlNum.setNum(rs.getInt("num"));
				return urlNum;
			}
		});
	}

}
