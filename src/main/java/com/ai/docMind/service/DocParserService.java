package com.ai.docMind.service;

import com.ai.docMind.exception.DocumentProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocParserService {

    private static final Logger logger = LoggerFactory.getLogger(DocParserService.class);

    public List<Document> parse(MultipartFile file) {

        String fileName = (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "document";
        String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octat-stream";

        logger.info("Parsing doc: {} size: {} bytes, contentType: {}", fileName, file.getSize(), contentType);

        try{
            Resource resource = new ByteArrayResource(file.getBytes()){
                @Override
                public @Nullable String getFileName(){
                    return fileName;
                }
            };

            if(fileName.toLowerCase().endsWith(".pdf") || contentType.contains("pdf")){
                return parsedPdf(resource);
            } else {
                return parsedGenericFile(resource);
            }

        } catch (IOException e){
            logger.error("Failed to read file bytes: {}",fileName, e);
            throw new DocumentProcessingException("Could not read uploaded file: " + fileName + e.getMessage());
        } catch (Exception e){
            logger.error("Error during document parsing: {}",fileName, e);
            throw new DocumentProcessingException("Failed to parse document content: " + fileName + e.getMessage());
        }

    }

    private List<Document> parsedGenericFile(Resource resource) {
        return new ArrayList<>();
    }

    private List<Document> parsedPdf(Resource resource) {
        return new ArrayList<>();
    }
}
