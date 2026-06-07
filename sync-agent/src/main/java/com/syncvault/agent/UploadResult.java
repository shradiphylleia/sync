package com.syncvault.agent;

public record UploadResult(
		String fileName,
		int uploadedChunks,
		int reusedChunks
) {
}
