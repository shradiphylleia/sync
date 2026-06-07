package com.syncvault.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "syncvault.storage")
public record StorageProperties(
		String endpoint,
		String accessKey,
		String secretKey,
		String bucket
) {
}
