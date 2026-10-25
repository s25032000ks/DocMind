package com.ai.docMind.controller;

import com.ai.docMind.dto.ApiResponse;
import com.ai.docMind.dto.DocumentMetadataDTO;
import com.ai.docMind.dto.DocumentResponseDTO;
import com.ai.docMind.entity.Users;
import com.ai.docMind.service.DocumentMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management Controller",
        description = "Endpoint for uploading, listing and managing documents and their vector embeddings."
)
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentMetaDataService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and index doc(PDF, DOCX, TXT, MD, CSV)",
        description = "This api is used to upload index document file")
    public ResponseEntity<ApiResponse> uploadDocument(@RequestParam("file") MultipartFile file,
                                                      Authentication authentication){
        Users user = (Users) authentication.getPrincipal();
        DocumentResponseDTO documentResponseDTO = documentService.uploadAndProcess(file, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .builder()
                        .success(true)
                        .data(documentResponseDTO)
                        .message("Document Uploaded")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and index multiple doc(PDF, DOCX, TXT, MD, CSV) simultaneously",
            description = "This api is used to upload index multiple document files")
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> uploadMultipleDocuments(@RequestParam("files") List<MultipartFile> files,
                                                                                          Authentication authentication){
        Users user = (Users) authentication.getPrincipal();
        List<DocumentResponseDTO> documentResponseList = documentService.uploadMultipleDocuments(files, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<List<DocumentResponseDTO>>builder()
                        .success(true)
                        .data(documentResponseList)
                        .message("Documents Uploaded Successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Operation(summary = "Get all documents metadata",
            description = "This api is used to get all documents metadata")
    public ResponseEntity<ApiResponse<List<DocumentMetadataDTO>>> getAllDocuments(){
        List<DocumentMetadataDTO> documentResponse = documentService.getAllDocuments();
        return ResponseEntity.ok(ApiResponse.<List<DocumentMetadataDTO>>builder()
                        .message("Document Fetched Successfully")
                        .data(documentResponse)
                        .success(true)
                        .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/user")
    @Operation(summary = "Get all documents metadata",
            description = "This api is used to get all documents metadata")
    public ResponseEntity<ApiResponse<List<DocumentMetadataDTO>>> getAllDocumentsByUser(Authentication authentication){
        Users user = (Users) authentication.getPrincipal();
        List<DocumentMetadataDTO> documentResponse = documentService.getAllDocumentsByUser(user);
        return ResponseEntity.ok(ApiResponse.<List<DocumentMetadataDTO>>builder()
                .message("Document Fetched Successfully")
                .data(documentResponse)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{docId}")
    @Operation(summary = "Get metadata of specific Document by ID ",
            description = "This api is used to get metadata of specific Document by ID")
    public ResponseEntity<ApiResponse> getDocumentById(@PathVariable UUID docId){
        DocumentMetadataDTO documentResponse = documentService.getDocumentById(docId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data(documentResponse)
                        .message("Document Fetched Successfully")
                        .success(true)
                        .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{docId}")
    @Operation(summary = "Delete a document and purge its vector embedding from vector store",
            description = "This api is used to delete a document and purge its vector embedding from vector store")
    public ResponseEntity<ApiResponse> deleteDocumentById(@PathVariable UUID docId){
        documentService.deleteDocumentById(docId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data(null)
                        .message("Document Deleted Successfully")
                        .success(true)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
