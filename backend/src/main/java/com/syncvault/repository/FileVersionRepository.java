package com.syncvault.repository;

import com.syncvault.entity.FileVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileVersionRepository extends JpaRepository<FileVersionEntity, UUID> {

	List<FileVersionEntity> findByFileIdOrderByVersionNumberAsc(UUID fileId);
}
