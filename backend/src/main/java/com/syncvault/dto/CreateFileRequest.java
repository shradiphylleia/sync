package com.syncvault.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFileRequest(
		@NotBlank String path,
		Long sizeBytes
) {
}
