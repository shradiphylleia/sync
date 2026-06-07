package com.syncvault.controller;

import com.syncvault.dto.VersionResponse;
import com.syncvault.service.FileVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileVersionController {

	private final FileVersionService fileVersionService;

	@PostMapping("/{fileId}/versions")
	@ResponseStatus(HttpStatus.CREATED)
	public VersionResponse createNewVersion(@PathVariable UUID fileId) {
		return fileVersionService.createNewVersion(fileId);
	}

	@GetMapping("/{fileId}/versions")
	public List<VersionResponse> getVersions(@PathVariable UUID fileId) {
		return fileVersionService.getVersions(fileId);
	}
}
