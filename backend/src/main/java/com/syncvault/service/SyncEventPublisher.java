package com.syncvault.service;

import com.syncvault.dto.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(SyncEventPublisher.class);
	private static final String CHANNEL = "sync-events";

	private final StringRedisTemplate redisTemplate;

	public void publish(SyncEvent event) {
		redisTemplate.convertAndSend(CHANNEL, toJson(event));
		log.info("Published sync event: file={}", event.fileName());
	}

	private String toJson(SyncEvent event) {
		return "{"
				+ "\"fileName\":\"" + escape(event.fileName()) + "\","
				+ "\"uploadedChunks\":" + event.uploadedChunks() + ","
				+ "\"reusedChunks\":" + event.reusedChunks() + ","
				+ "\"timestamp\":\"" + event.timestamp() + "\""
				+ "}";
	}

	private String escape(String value) {
		if (value == null) {
			return "";
		}

		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"");
	}
}
