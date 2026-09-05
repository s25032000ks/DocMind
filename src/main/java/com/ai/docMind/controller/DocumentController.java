package com.ai.docMind.controller;

import com.ai.docMind.dto.ApiResponse;
import com.ai.docMind.dto.DocumentResponseDTO;
import com.ai.docMind.service.DocumentMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management Controller",
        description = "Endpoint for uploading, listing and managing documents and their vector embeddings."
)
public class DocumentController {

    private final DocumentMetaDataService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and index doc(PDF, DOCX, TXT, MD, CSV)",
        description = "This api is used to upload index document file")
    public ResponseEntity<ApiResponse> uploadDocument(@RequestParam("file") MultipartFile file){
        DocumentResponseDTO documentResponseDTO = documentService.uploadAndProcess(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .builder()
                        .success(true)
                        .data(documentResponseDTO)
                        .message("Document Uploaded")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
