package com.syncvault.service;

import com.syncvault.dto.ChunkUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileUploadService {

	private final ChunkingService chunkingService;
	private final ChunkStorageService chunkStorageService;

	public ChunkUploadResponse upload(MultipartFile file) {
		List<ChunkingService.ChunkData> chunks = chunkingService.chunk(file);

		for (ChunkingService.ChunkData chunk : chunks) {
			chunkStorageService.uploadChunk(chunk.hash(), chunk.data());
		}

		return new ChunkUploadResponse(file.getOriginalFilename(), chunks.size(), chunks.size());
	}
}
