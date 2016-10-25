package com.daydays.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class OriginalLogDaoImpl {
	@Resource
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = Logger.getLogger(OriginalLogDaoImpl.class);

	private String createTableSql = "CREATE TABLE if not exists $tablename (\n"
			+ "id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n"
			+ " log_info TEXT DEFAULT NULL COMMENT '日志详情',\n" + "PRIMARY KEY (id)\n"
			+ "	)  ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8 COMMENT='源日志';\n";

	private String orgLogSizeSql = "SELECT \n    COUNT(1)\nFROM\n    $tablename";

	private String orgLogQuerySql = "SELECT \n    log_info\nFROM\n    $tablename\nLIMIT $starIndex , $limitNum";

	public void createLogtable(String tableName) {
		try {
			String tempCreateSql = createTableSql.replace("$tablename", tableName);
			this.jdbcTemplate.execute(tempCreateSql);
		} catch (Exception e) {
			logger.warn("创建表失败，table.name=" + tableName);
			throw e;
		}
	}

	public int addOriginalLog(Collection<String> orgLogs, String tempTableName) {
		if (CollectionUtils.isEmpty(orgLogs)) {
			return 0;
		}

		String sql = "insert into " + tempTableName + "(log_info) values ";
		Iterator<String> iterator = orgLogs.iterator();
		while (iterator.hasNext()) {
			String originalLog = iterator.next();
			sql += "('" + originalLog + "'),";
		}
		sql = sql.substring(0, sql.length() - 1);
		int result = this.jdbcTemplate.update(sql);
		this.jdbcTemplate.execute("commit;");
		return result;
	}

	public int queryOriginalLogSize(String orgLogTableName) {
		String tempUrl = orgLogSizeSql.replace("$tablename", orgLogTableName);
		return this.jdbcTemplate.queryForObject(tempUrl, Integer.class);
	}

	public List<String> queryOriginalLog(String orgLogTableName, int pageNum, int pageSize) {
		String tempUrl = orgLogQuerySql.replace("$tablename", orgLogTableName)
				.replace("$starIndex", String.valueOf(pageNum)).replace("$limitNum", String.valueOf(pageSize));

		return this.jdbcTemplate.query(tempUrl, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("log_info");
			}
		});
	}
}
