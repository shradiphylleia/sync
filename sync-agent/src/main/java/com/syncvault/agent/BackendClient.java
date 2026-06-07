package com.syncvault.agent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackendClient {

	private static final Pattern FILE_NAME_PATTERN = Pattern.compile("\"fileName\"\\s*:\\s*\"([^\"]*)\"");
	private static final Pattern UPLOADED_CHUNKS_PATTERN = Pattern.compile("\"uploadedChunks\"\\s*:\\s*(\\d+)");
	private static final Pattern REUSED_CHUNKS_PATTERN = Pattern.compile("\"reusedChunks\"\\s*:\\s*(\\d+)");

	private final HttpClient httpClient;
	private final URI uploadUri;

	public BackendClient(String uploadUrl) {
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.build();
		this.uploadUri = URI.create(uploadUrl);
	}

	public UploadResult upload(Path filePath) throws IOException, InterruptedException {
		String boundary = "----SyncVaultBoundary" + UUID.randomUUID();
		HttpRequest request = HttpRequest.newBuilder(uploadUri)
				.timeout(Duration.ofMinutes(2))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(createMultipartBody(filePath, boundary))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IllegalStateException("Backend returned HTTP " + response.statusCode() + ": " + response.body());
		}

		return parseUploadResult(response.body());
	}

	private HttpRequest.BodyPublisher createMultipartBody(Path filePath, String boundary) throws IOException {
		String fileName = filePath.getFileName().toString();
		String contentType = Files.probeContentType(filePath);
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		String header = "--" + boundary + "\r\n"
				+ "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n"
				+ "Content-Type: " + contentType + "\r\n\r\n";
		String footer = "\r\n--" + boundary + "--\r\n";

		return HttpRequest.BodyPublishers.concat(
				HttpRequest.BodyPublishers.ofString(header),
				HttpRequest.BodyPublishers.ofFile(filePath),
				HttpRequest.BodyPublishers.ofString(footer)
		);
	}

	private UploadResult parseUploadResult(String responseBody) {
		return new UploadResult(
				readString(responseBody, FILE_NAME_PATTERN),
				readInt(responseBody, UPLOADED_CHUNKS_PATTERN),
				readInt(responseBody, REUSED_CHUNKS_PATTERN)
		);
	}

	private String readString(String responseBody, Pattern pattern) {
		Matcher matcher = pattern.matcher(responseBody);
		if (!matcher.find()) {
			throw new IllegalStateException("Could not read fileName from backend response: " + responseBody);
		}
		return matcher.group(1);
	}

	private int readInt(String responseBody, Pattern pattern) {
		Matcher matcher = pattern.matcher(responseBody);
		if (!matcher.find()) {
			throw new IllegalStateException("Could not read chunk count from backend response: " + responseBody);
		}
		return Integer.parseInt(matcher.group(1));
	}
}
