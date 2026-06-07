package com.syncvault.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class MinioConfig {

	@Bean
	public MinioClient minioClient(StorageProperties storageProperties) {
		return MinioClient.builder()
				.endpoint(storageProperties.endpoint())
				.credentials(storageProperties.accessKey(), storageProperties.secretKey())
				.build();
	}
}
