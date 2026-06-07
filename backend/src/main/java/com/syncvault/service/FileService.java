package com.syncvault.service;

import com.syncvault.dto.CreateFileRequest;
import com.syncvault.dto.FileResponse;
import com.syncvault.entity.FileEntity;
import com.syncvault.exception.FileAlreadyExistsException;
import com.syncvault.repository.FileRepository;
import com.syncvault.repository.FileVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileRepository fileRepository;
	private final FileVersionRepository fileVersionRepository;
	private final FileVersionService fileVersionService;

	@Transactional
	public FileResponse createFile(CreateFileRequest request) {
		if (fileRepository.existsByPath(request.path())) {
			throw new FileAlreadyExistsException(request.path());
		}

		FileEntity file = new FileEntity();
		file.setPath(request.path());
		file.setSizeBytes(request.sizeBytes());
		file.setCurrentVersion(1L);

		FileEntity savedFile = fileRepository.save(file);
		fileVersionRepository.save(fileVersionService.createVersionEntity(savedFile, 1L));

		return toResponse(savedFile);
	}

	public FileResponse getFile(UUID id) {
		FileEntity file = fileRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

		return toResponse(file);
	}

	public List<FileResponse> getAllFiles() {
		return fileRepository.findAll()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private FileResponse toResponse(FileEntity file) {
		return new FileResponse(
				file.getId(),
				file.getPath(),
				file.getSizeBytes(),
				file.getCurrentVersion()
		);
	}
}
