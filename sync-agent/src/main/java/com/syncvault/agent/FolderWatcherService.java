package com.syncvault.agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FolderWatcherService {

	private final Path watchedFolder;
	private final BackendClient backendClient;

	public FolderWatcherService(Path watchedFolder, BackendClient backendClient) {
		this.watchedFolder = watchedFolder;
		this.backendClient = backendClient;
	}

	public void watch() throws IOException, InterruptedException {
		Files.createDirectories(watchedFolder);

		try (WatchService watchService = watchedFolder.getFileSystem().newWatchService()) {
			watchedFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			System.out.println("Watching folder:");
			System.out.println(watchedFolder.toAbsolutePath());

			while (true) {
				WatchKey watchKey = watchService.take();
				handleEvents(watchKey);

				if (!watchKey.reset()) {
					throw new IllegalStateException("Folder watcher is no longer valid");
				}
			}
		}
	}

	private void handleEvents(WatchKey watchKey) {
		for (WatchEvent<?> event : watchKey.pollEvents()) {
			if (event.kind() != StandardWatchEventKinds.ENTRY_CREATE) {
				continue;
			}

			Path fileName = (Path) event.context();
			Path filePath = watchedFolder.resolve(fileName);
			uploadIfFile(filePath);
		}
	}

	private void uploadIfFile(Path filePath) {
		if (Files.isDirectory(filePath)) {
			return;
		}

		try {
			System.out.println("Detected file:");
			System.out.println(filePath.getFileName());
			System.out.println("Uploading file:");
			System.out.println(filePath.getFileName());

			UploadResult uploadResult = backendClient.upload(filePath);

			System.out.println("Upload successful:");
			System.out.println(uploadResult.fileName());
			System.out.println("uploadedChunks=" + uploadResult.uploadedChunks());
			System.out.println("reusedChunks=" + uploadResult.reusedChunks());
		} catch (Exception e) {
			System.err.println("Upload failed:");
			System.err.println(filePath.getFileName());
			System.err.println(e.getMessage());
		}
	}
}
