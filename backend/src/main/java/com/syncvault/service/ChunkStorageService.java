package com.syncvault.service;

import com.syncvault.config.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class ChunkStorageService {

	private static final Logger log = LoggerFactory.getLogger(ChunkStorageService.class);

	private final MinioClient minioClient;
	private final StorageProperties storageProperties;

	public void uploadChunk(String hash, byte[] data) {
		try {
			log.info("Uploading new chunk: {}", hash);
			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket(storageProperties.bucket())
							.object(hash)
							.stream(new ByteArrayInputStream(data), (long) data.length, -1L)
							.build()
			);
			log.info("Uploaded chunk: {}", hash);
		} catch (Exception e) {
			throw new IllegalStateException("Could not upload chunk to MinIO", e);
		}
	}

	public boolean chunkExists(String hash) {
		try {
			minioClient.statObject(
					StatObjectArgs.builder()
							.bucket(storageProperties.bucket())
							.object(hash)
							.build()
			);
			return true;
		} catch (ErrorResponseException e) {
			if ("NoSuchKey".equals(e.errorResponse().code())) {
				return false;
			}
			throw new IllegalStateException("Could not check chunk in MinIO", e);
		} catch (Exception e) {
			throw new IllegalStateException("Could not check chunk in MinIO", e);
		}
	}

	public boolean bucketExists() {
		try {
			return minioClient.bucketExists(
					BucketExistsArgs.builder()
							.bucket(storageProperties.bucket())
							.build()
			);
		} catch (Exception e) {
			throw new IllegalStateException("Could not check MinIO bucket", e);
		}
	}

	public void createBucketIfMissing() {
		try {
			if (!bucketExists()) {
				minioClient.makeBucket(
						MakeBucketArgs.builder()
								.bucket(storageProperties.bucket())
								.build()
				);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Could not create MinIO bucket", e);
		}
	}
}
