package com.syncvault.service;

import com.syncvault.dto.VersionResponse;
import com.syncvault.entity.FileEntity;
import com.syncvault.entity.FileVersionEntity;
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
public class FileVersionService {

	private final FileRepository fileRepository;
	private final FileVersionRepository fileVersionRepository;

	@Transactional
	public VersionResponse createNewVersion(UUID fileId) {
		FileEntity file = getFileEntity(fileId);
		Long nextVersion = file.getCurrentVersion() + 1;

		file.setCurrentVersion(nextVersion);

		FileVersionEntity version = createVersionEntity(file, nextVersion);
		return toResponse(fileVersionRepository.save(version));
	}

	public List<VersionResponse> getVersions(UUID fileId) {
		if (!fileRepository.existsById(fileId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
		}

		return fileVersionRepository.findByFileIdOrderByVersionNumberAsc(fileId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	FileVersionEntity createVersionEntity(FileEntity file, Long versionNumber) {
		FileVersionEntity version = new FileVersionEntity();
		version.setFile(file);
		version.setVersionNumber(versionNumber);
		version.setSizeBytes(file.getSizeBytes());
		return version;
	}

	private FileEntity getFileEntity(UUID fileId) {
		return fileRepository.findById(fileId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
	}

	private VersionResponse toResponse(FileVersionEntity version) {
		return new VersionResponse(
				version.getId(),
				version.getVersionNumber(),
				version.getSizeBytes(),
				version.getCreatedAt()
		);
	}
}
