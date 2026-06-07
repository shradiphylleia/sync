package com.syncvault.service;

import com.syncvault.dto.ChunkAnalysisResponse;
import com.syncvault.dto.ChunkInfoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

@Service
public class ChunkingService {

	private static final Logger log = LoggerFactory.getLogger(ChunkingService.class);
	private static final int CHUNK_SIZE_BYTES = 4 * 1024 * 1024;

	public ChunkAnalysisResponse analyze(MultipartFile file) {
		List<ChunkInfoResponse> chunks = chunk(file).stream()
				.map(chunk -> new ChunkInfoResponse(chunk.index(), chunk.hash(), chunk.sizeBytes()))
				.toList();
		return new ChunkAnalysisResponse(file.getOriginalFilename(), chunks.size(), chunks);
	}

	public List<ChunkData> chunk(MultipartFile file) {
		log.info("Analyzing file: {}", file.getOriginalFilename());

		if (file.isEmpty()) {
			log.info("Completed analysis. totalChunks=0");
			return List.of();
		}

		try (InputStream inputStream = file.getInputStream()) {
			List<ChunkData> chunks = readChunkData(inputStream);
			log.info("Completed analysis. totalChunks={}", chunks.size());
			return chunks;
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded file", e);
		}
	}

	public String sha256(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(data));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm is not available", e);
		}
	}

	private List<ChunkData> readChunkData(InputStream inputStream) throws IOException {
		List<ChunkData> chunks = new ArrayList<>();
		int index = 0;

		byte[] chunkData;
		while ((chunkData = inputStream.readNBytes(CHUNK_SIZE_BYTES)).length > 0) {
			String hash = sha256(chunkData);
			long sizeBytes = chunkData.length;
			chunks.add(new ChunkData(index, hash, sizeBytes, chunkData));
			log.info("Chunk {} hash={} size={}", index, hash, sizeBytes);
			index++;
		}

		return chunks;
	}

	public record ChunkData(
			int index,
			String hash,
			long sizeBytes,
			byte[] data
	) {
	}
}
