package com.daydays.utils;

import java.util.Collection;

/**
 * 执行接口
 * @author dingpc
 *
 */
public interface IExecutable {

	<T> void execute(Collection<T> collectoins);
}
