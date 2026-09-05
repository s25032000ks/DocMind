package com.ai.docMind.repository;

import com.ai.docMind.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface DocumentMetaDataRepository extends JpaRepository<DocumentMetadata, UUID> {
}
