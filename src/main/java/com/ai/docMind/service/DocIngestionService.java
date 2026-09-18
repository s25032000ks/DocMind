package com.ai.docMind.service;

import com.ai.docMind.entity.DocumentMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocIngestionService {
    public int ingest(DocumentMetadata documentMetadata, List<Document> parsedDoc) {
        return 1;
    }
}
