package com.syncvault.dto;

public record ChunkInfoResponse(
		int index,
		String hash,
		Long sizeBytes
) {
}
