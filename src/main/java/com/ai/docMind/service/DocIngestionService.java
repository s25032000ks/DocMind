package com.ai.docMind.service;

import com.ai.docMind.config.AppProperties;
import com.ai.docMind.entity.DocumentMetadata;
import com.ai.docMind.entity.DocumentStatus;
import com.ai.docMind.exception.DocumentProcessingException;
import com.ai.docMind.repository.DocumentMetaDataRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocIngestionService {

    private static final Logger logger = LoggerFactory.getLogger(DocIngestionService.class);

    private final VectorStore vectorStore;
    private final DocumentMetaDataRepository documentMetaDataRepository;
    private final AppProperties appProperties;

    public int ingest(DocumentMetadata documentMetadata, List<Document> parsedDoc) {

        logger.info("Ingesting document [id: {}, name: {}, page: {}]",
                documentMetadata.getUuid(), documentMetadata.getFileName(), documentMetadata.getFileSize());

        try{
            documentMetadata.setStatus(DocumentStatus.PROCESSING);
            documentMetadata.setTotalPages(parsedDoc.size());

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                    .withChunkSize(appProperties.getRag().getChunkSize())
                    .withMaxNumChunks(appProperties.getRag().getMaxNumChunks())
                    .withMinChunkLengthToEmbed(appProperties.getRag().getMinChunkLengthToEmbed())
                    .withMinChunkSizeChars(appProperties.getRag().getMinChunkSizeChars())
                    .withKeepSeparator(true)
                    .build();

            List<Document> chunks = tokenTextSplitter.apply(parsedDoc);
            if(chunks.isEmpty()){
                documentMetadata.setStatus(DocumentStatus.FAILED);
                documentMetadata.setErrorMessage("Document appears to be empty or unscannable");
                documentMetaDataRepository.save(documentMetadata);
                return 0;
            }

            List<Document> enrichedChunks = new ArrayList<>();
            for(int i = 0; i < chunks.size(); i++){
                Document chunk = chunks.get(i);
                Map<String, Object> enrichMetadata = new HashMap<>(chunk.getMetadata());
                enrichMetadata.put("documentId", documentMetadata.getUuid().toString());
                enrichMetadata.put("fileName", documentMetadata.getFileName());
                enrichMetadata.put("contentType", documentMetadata.getContentType());
                enrichMetadata.put("hunkIndex", i);
                Object pageNumber = chunk.getMetadata().get("page_number");
                if(pageNumber == null){
                    pageNumber = chunk.getMetadata().get("pageNumber");
                }

                if(pageNumber != null)
                    enrichMetadata.put("pageNumber", pageNumber);

                Document enrichDoc = new Document(chunk.getText(), enrichMetadata);
                enrichedChunks.add(enrichDoc);
            }

            logger.info("Writing {} vector chunks PgVectorStore for document: {}", enrichedChunks.size(), documentMetadata.getFileName());
            vectorStore.add(enrichedChunks);

            documentMetadata.setStatus(DocumentStatus.INDEXED);
            documentMetadata.setTotalChunks(enrichedChunks.size());
            documentMetadata.setErrorMessage(null);
            documentMetaDataRepository.save(documentMetadata);
            logger.info("Successfully Indexed Document [id: {}, name: {}, chunks: {}]", documentMetadata.getUuid(), documentMetadata.getFileName(), enrichedChunks.size());
            return enrichedChunks.size();
        }catch (Exception e) {
            logger.error("Failed to ingest document into vector store: {}", documentMetadata.getFileName(), e);
            documentMetadata.setStatus(DocumentStatus.FAILED);
            documentMetadata.setErrorMessage(e.getMessage());
            documentMetaDataRepository.save(documentMetadata);
            throw new DocumentProcessingException("Failed to index document" + e.getMessage());
        }
    }
}
