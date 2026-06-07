package com.syncvault.controller;

import com.syncvault.dto.CreateFileRequest;
import com.syncvault.dto.FileResponse;
import com.syncvault.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FileResponse createFile(@Valid @RequestBody CreateFileRequest request) {
		return fileService.createFile(request);
	}

	@GetMapping
	public List<FileResponse> getAllFiles() {
		return fileService.getAllFiles();
	}

	@GetMapping("/{id}")
	public FileResponse getFile(@PathVariable UUID id) {
		return fileService.getFile(id);
	}
}
