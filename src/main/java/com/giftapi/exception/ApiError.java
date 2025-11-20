package com.giftapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {

	private final String message;

	private final HttpStatus status;

	public ApiError(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	public static ApiError of(String message) {
		return new ApiError(message, null);
	}

}
