package com.syncvault.dto;

import java.time.Instant;

public record SyncEvent(
		String fileName,
		int uploadedChunks,
		int reusedChunks,
		Instant timestamp
) {
}
