package com.ai.docMind.controller;

import com.ai.docMind.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management Controller",
        description = "Endpoint for uploading, listing and managing documents and their vector embeddings."
)
public class DocumentController {

    private final DocumentService documentService;
}
