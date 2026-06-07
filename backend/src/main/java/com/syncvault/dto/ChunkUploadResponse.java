package com.syncvault.dto;

public record ChunkUploadResponse(
		String fileName,
		int totalChunks,
		int uploadedChunks
) {
}
