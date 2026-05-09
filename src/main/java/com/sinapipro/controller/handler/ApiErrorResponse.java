package com.sinapipro.controller.handler;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {

	private final LocalDateTime timestamp;
	private final int status;
	private final String error;
	private final String message;
	private final List<String> details;
	private final String path;

	private ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, List<String> details, String path) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.details = details;
		this.path = path;
	}

	public static ApiErrorResponse of(int status, String error, String message, String path) {
		return new ApiErrorResponse(LocalDateTime.now(), status, error, message, List.of(), path);
	}

	public static ApiErrorResponse validation(int status, String error, String message, List<String> details, String path) {
		return new ApiErrorResponse(LocalDateTime.now(), status, error, message, details, path);
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getDetails() {
		return details;
	}

	public String getPath() {
		return path;
	}
}
