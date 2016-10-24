package com.daydays.domain;

public enum LogLevel {
	DEBUG(1), WARN(2), ERROR(3);

	int id;

	LogLevel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
