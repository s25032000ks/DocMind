package com.ai.docMind.service;

import com.ai.docMind.exception.DocumentProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
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
                public @Nullable String getFilename() {
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
        TikaDocumentReader documentReader = new TikaDocumentReader(resource);
        return documentReader.read();
    }

    private List<Document> parsedPdf(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageBottomMargin(0)
                .withPageTopMargin(0)
                .build();
        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(resource, config);
        return documentReader.read();
    }
}
