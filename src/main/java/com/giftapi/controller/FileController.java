package com.giftapi.controller;

import com.giftapi.service.FastCsvImportService;
import com.giftapi.service.FileService;
import com.giftapi.service.FileServiceMoreTheOneThread;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

	private final FastCsvImportService fileService;

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void upload(@RequestParam("file") MultipartFile file) throws Exception {
		fileService.upload(file);
	}

}
