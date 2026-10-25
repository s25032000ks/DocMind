package com.ai.docMind.repository;

import com.ai.docMind.entity.DocumentMetadata;
import com.ai.docMind.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentMetaDataRepository extends JpaRepository<DocumentMetadata, UUID> {
    List<DocumentMetadata> findAllByOrderByCreatedAtDesc();

    List<DocumentMetadata> findAByUserOrderByCreatedAtDesc(Users user);
}
