package com.giftapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GiftApiException extends RuntimeException {

	private final ApiError apiError;

	public GiftApiException(ApiError apiError) {
		this.apiError = apiError;
	}

	public static GiftApiException of(String message, HttpStatus status) {
		return new GiftApiException(new ApiError(message, status));
	}

	public static GiftApiException entityNotFound(String message) {
		return new GiftApiException(new ApiError(message, HttpStatus.NOT_FOUND));
	}

}
