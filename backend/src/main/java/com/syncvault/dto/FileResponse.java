package com.syncvault.dto;

import java.util.UUID;

public record FileResponse(
		UUID id,
		String path,
		Long sizeBytes,
		Long currentVersion
) {
}
