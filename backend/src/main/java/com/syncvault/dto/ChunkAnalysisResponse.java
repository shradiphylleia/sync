package com.syncvault.dto;

import java.util.List;

public record ChunkAnalysisResponse(
		String fileName,
		int totalChunks,
		List<ChunkInfoResponse> chunks
) {
}
