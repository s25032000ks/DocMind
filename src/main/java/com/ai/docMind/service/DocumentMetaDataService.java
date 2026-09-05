package com.ai.docMind.service;

import com.ai.docMind.dto.DocumentResponseDTO;
import com.ai.docMind.entity.DocumentMetadata;
import com.ai.docMind.entity.DocumentStatus;
import com.ai.docMind.repository.DocumentMetaDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentMetaDataService {

    private final DocumentMetaDataRepository metaDataRepository;
    private final ParserService parserService;
    private final IngestionService ingestionService;

    public DocumentResponseDTO uploadAndProcess(MultipartFile file) {
        String fileName = (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "document";
        String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octat-stream";

        DocumentMetadata documentMetadata = DocumentMetadata.builder()
                .fileName(fileName)
                .contentType(contentType)
                .status(DocumentStatus.UPLOADING)
                .fileSize(file.getSize())
                .build();

        documentMetadata = metaDataRepository.save(documentMetadata);

        List<Document> parsedDoc = parserService.parse(file);

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
}
