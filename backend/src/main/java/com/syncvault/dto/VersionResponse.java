package com.syncvault.dto;

import java.time.Instant;
import java.util.UUID;

public record VersionResponse(
		UUID id,
		Long versionNumber,
		Long sizeBytes,
		Instant createdAt
) {
}
