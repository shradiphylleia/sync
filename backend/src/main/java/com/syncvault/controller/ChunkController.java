package com.syncvault.controller;

import com.syncvault.dto.ChunkAnalysisResponse;
import com.syncvault.service.ChunkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chunks")
@RequiredArgsConstructor
public class ChunkController {

	private final ChunkingService chunkingService;

	@PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ChunkAnalysisResponse analyze(@RequestPart("file") MultipartFile file) {
		return chunkingService.analyze(file);
	}
}
