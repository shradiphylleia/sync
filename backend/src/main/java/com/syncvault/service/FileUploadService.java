package com.syncvault.service;

import com.syncvault.dto.ChunkUploadResponse;
import com.syncvault.dto.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FileUploadService {

	private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

	private final ChunkingService chunkingService;
	private final ChunkStorageService chunkStorageService;
	private final SyncEventPublisher syncEventPublisher;

	public ChunkUploadResponse upload(MultipartFile file) {
		List<ChunkingService.ChunkData> chunks = chunkingService.chunk(file);
		chunkStorageService.createBucketIfMissing();

		int uploadedChunks = 0;
		int reusedChunks = 0;

		for (ChunkingService.ChunkData chunk : chunks) {
			if (chunkStorageService.chunkExists(chunk.hash())) {
				log.info("Chunk already exists: {}", chunk.hash());
				reusedChunks++;
				continue;
			}

			chunkStorageService.uploadChunk(chunk.hash(), chunk.data());
			uploadedChunks++;
		}

		ChunkUploadResponse response = new ChunkUploadResponse(
				file.getOriginalFilename(),
				chunks.size(),
				uploadedChunks,
				reusedChunks,
				calculateDeduplicationRatio(chunks.size(), reusedChunks)
		);

		syncEventPublisher.publish(new SyncEvent(
				response.fileName(),
				response.uploadedChunks(),
				response.reusedChunks(),
				Instant.now()
		));

		return response;
	}

	private String calculateDeduplicationRatio(int totalChunks, int reusedChunks) {
		if (totalChunks == 0) {
			return "0.00%";
		}

		double ratio = ((double) reusedChunks / totalChunks) * 100;
		return String.format(Locale.ROOT, "%.2f%%", ratio);
	}
}
