package com.giftapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler({ GiftApiException.class })
	public ResponseEntity<ApiError> handleBadRequest(GiftApiException ex) {
		return ResponseEntity.status(ex.getApiError().getStatus()).body(ex.getApiError());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAll(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.of("Internal server error"));
	}
}