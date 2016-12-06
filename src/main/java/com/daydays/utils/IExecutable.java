package com.daydays.utils;

import java.util.List;

/**
 * 执行接口
 * @author dingpc
 *
 */
public interface IExecutable {

	<T> void execute(List<T> collectoins);
}
