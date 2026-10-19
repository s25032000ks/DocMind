package com.ai.docMind.service;

import com.ai.docMind.dto.DocumentMetadataDTO;
import com.ai.docMind.dto.DocumentResponseDTO;
import com.ai.docMind.entity.DocumentMetadata;
import com.ai.docMind.entity.DocumentStatus;
import com.ai.docMind.exception.ResourceNotFoundException;
import com.ai.docMind.repository.DocumentMetaDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentMetaDataService {

    private final DocumentMetaDataRepository metaDataRepository;
    private final DocParserService docParserService;
    private final DocIngestionService ingestionService;
    private final ModelMapper modelMapper;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor=Exception.class)
    public DocumentResponseDTO uploadAndProcess(MultipartFile file) {
        String fileName = (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "document";
        String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octet-stream";

        DocumentMetadata documentMetadata = DocumentMetadata.builder()
                .fileName(fileName)
                .contentType(contentType)
                .status(DocumentStatus.UPLOADING)
                .fileSize(file.getSize())
                .build();

        documentMetadata = metaDataRepository.save(documentMetadata);

        List<Document> parsedDoc = docParserService.parse(file);

        int chunkCreated = ingestionService.ingest(documentMetadata, parsedDoc);

        return DocumentResponseDTO.builder()
                .uuid(documentMetadata.getUuid())
                .chunkCreated(chunkCreated)
                .fileName(documentMetadata.getFileName())
                .fileSize(documentMetadata.getFileSize())
                .status(documentMetadata.getStatus())
                .message("Document successfully processed and indexed")
                .build();
    }

    public List<DocumentResponseDTO> uploadMultipleDocuments(List<MultipartFile> files) {
        List<DocumentResponseDTO> documentResponseDTOList = new ArrayList<>();
        for(MultipartFile file : files){
            DocumentResponseDTO documentResponseDTO = this.uploadAndProcess(file);
            documentResponseDTOList.add(documentResponseDTO);
        }
        return documentResponseDTOList;
    }

    public List<DocumentMetadataDTO> getAllDocuments() {
        List<DocumentMetadata> allDocumentMetadata = metaDataRepository.findAllByOrderByCreatedAtDesc();
        return allDocumentMetadata.stream()
                .map(documentMetadata -> modelMapper.map(documentMetadata, DocumentMetadataDTO.class))
                .toList();
    }

    public DocumentMetadataDTO getDocumentById(UUID docId) {
        DocumentMetadata documentMetadata = metaDataRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + docId));
        return modelMapper.map(documentMetadata, DocumentMetadataDTO.class);
    }

    @Transactional
    public void deleteDocumentById(UUID docId) {
        DocumentMetadata documentMetadata = metaDataRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + docId));
        try{
            int deletedVectors = jdbcTemplate.update(
                    "DELETE FROM vector_store WHERE metadata->>'documentId' = ?",
                    docId.toString()
            );
            metaDataRepository.delete(documentMetadata);
            log.info("Deleted {} vector chunk for document id: {}", deletedVectors, docId);
        } catch (Exception e){
            log.error("could not delete vectors from vector store directly: {}", e.getMessage());
        }
    }
}
