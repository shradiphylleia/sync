package com.syncvault.repository;

import com.syncvault.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {

	boolean existsByPath(String path);
}
