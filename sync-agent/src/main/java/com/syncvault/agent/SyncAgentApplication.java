package com.syncvault.agent;

import java.nio.file.Path;

public class SyncAgentApplication {

	public static void main(String[] args) throws Exception {
		Path watchedFolder = Path.of("watched-folder");
		BackendClient backendClient = new BackendClient("http://localhost:8080/api/files/upload");
		FolderWatcherService folderWatcherService = new FolderWatcherService(watchedFolder, backendClient);

		System.out.println("Agent started.");
		folderWatcherService.watch();
	}
}
