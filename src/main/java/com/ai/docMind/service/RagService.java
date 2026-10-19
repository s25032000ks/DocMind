package com.ai.docMind.service;

import com.ai.docMind.config.AppProperties;
import com.ai.docMind.dto.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private static final Logger logger = LoggerFactory.getLogger(RagService.class);

    private final VectorStore vectorStore;
    private final AppProperties appProperties;
    private final ChatClient chatClient;

    public SearchResultDTO searchSimilarChunks(SearchRequestDTO searchRequestDTO){
        List<Document> matchedDocs = retrieveRelevantDocument(searchRequestDTO.query(),
                                                              searchRequestDTO.documentId(),
                                                              searchRequestDTO.topK(),
                                                              searchRequestDTO.similaritySearch());

        List<CitationDTO> citationDTOList = matchedDocs.stream().map(this::mapToCitation).toList();
        return SearchResultDTO.builder()
                .query(searchRequestDTO.query())
                .totalMatches(citationDTOList.size())
                .matches(citationDTOList)
                .build();
    }

    private CitationDTO mapToCitation(Document document) {
        Map<String, Object> meta = document.getMetadata();

        UUID docId = null;
        if(meta.get("documentId") != null){
            try {
                docId = UUID.fromString(meta.get("documentId").toString());
            } catch (Exception ignore){}
        }

        Integer chunkIndex = null;
        if(meta.get("chunkIndex") instanceof Number n){
            chunkIndex = n.intValue();
        }

        Integer pagenumber = null;
        if(meta.get("pageNumber") instanceof Number n){
            pagenumber = n.intValue();
        } else if(meta.get("page_number") instanceof Number n){
            pagenumber = n.intValue();
        }

        Double score = null;
        if(meta.get("distance") instanceof Number n){
            score = 1.0 - n.doubleValue();
        }

        return CitationDTO.builder()
                .documentId(docId)
                .fileName((String) meta.getOrDefault("fileName", "Unknown"))
                .chunkIndex(chunkIndex)
                .similarityScore(score)
                .pageNumber(pagenumber)
                .snippet(document.getText())
                .metadata(meta)
                .build();
    }

    private List<Document> retrieveRelevantDocument(@NotBlank(message = "Query cannot be blank") String query, UUID documentId, Integer topK, Double similaritySearch) {
        int effectiveTopK = (topK != null && topK > 0) ? topK : appProperties.getRag().getTopK();
        double effectiveSimilaritySearch = (similaritySearch != null && similaritySearch > 0.0) ? similaritySearch : appProperties.getRag().getSimilarityThreshold();

        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                .query(query)
                .topK(effectiveTopK)
                .similarityThreshold(effectiveSimilaritySearch);

        if (documentId != null){
            FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();
            searchRequestBuilder.filterExpression(filterExpressionBuilder.eq("documentId", documentId.toString()).build());
        }

        try{
            List<Document> documentList = vectorStore.similaritySearch(searchRequestBuilder.build());
            logger.info("Retrieved: {} chunks for query: '{}' (scoped docId: {})", documentList.size(), query, documentId);
            return documentList;
        } catch (Exception e){
            logger.error("Similarity Search failed for query: '{}'", query, e);
            return Collections.emptyList();
        }
    }

    public ChatResponseDTO askQuestions(ChatRequestDTO chatRequest){
        long startTime = System.currentTimeMillis();
        logger.info("Processing Query: '{}', scoped docId: {}", chatRequest.question(), chatRequest.documentId());

        List<Document> similarDocument = this.retrieveRelevantDocument(chatRequest.question(),
                                                                        chatRequest.documentId(),
                                                                        chatRequest.topK(),
                                                                        chatRequest.minSimilarity());

        List<CitationDTO> citationList = similarDocument.stream().map(this::mapToCitation).toList();

        String contextText = buildContextText(similarDocument);
        String prompt = buildPrompt(chatRequest.question(), contextText);

        String response = this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        long responseTime = System.currentTimeMillis() - startTime;
        logger.info("Completed Q&A in {} ms with {} citations", responseTime, citationList.size());

        return ChatResponseDTO.builder()
                .answer(response)
                .conversationId(chatRequest.conversationId() != null ? chatRequest.conversationId() : UUID.randomUUID().toString())
                .citationList(citationList)
                .responseTimeMs(responseTime)
                .build();
    }

    public Flux<String> streamQuestionAnswer(ChatRequestDTO chatRequest){
        logger.info("Streaming Query: '{}'", chatRequest.question());

        List<Document> similarDocument = this.retrieveRelevantDocument(chatRequest.question(),
                chatRequest.documentId(),
                chatRequest.topK(),
                chatRequest.minSimilarity());

        String contextText = buildContextText(similarDocument);
        String prompt = buildPrompt(chatRequest.question(), contextText);

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }

    private String buildPrompt(@NotBlank(message = "Question cannot be empty") String question, String contextText) {

        if (contextText != null && !contextText.isBlank()) {
            return String.format("""
                           Document Context:
                           ---------------------
                           %s
                           ---------------------
                           User Message / Question: %s
                           Instructions:
                           - If the user's question relates to the document context above, prioritize answering using that context and reference key sections.
                           - If the user is asking a general question, greeting, or discussing topics beyond the document context, respond helpfully and conversationally using your general knowledge while weaving in relevant document context if applicable
                    """,
                    contextText, question);
        } else {
            return String.format("""
                       User Message / Question:
                       %s
                       Instructions:
                          - Respond helpfully, accurately, and conversationally to the user's message using your broad knowledge base.
                    
                    """,
                    question);
        }

    }

    private String buildContextText(List<Document> similarDocument) {
        if(similarDocument == null || similarDocument.isEmpty())
            return "";

        return similarDocument.stream()
                .map(doc -> {
                    String fileName = (String) doc.getMetadata().getOrDefault("fileName", "Unknown File");
                    Object page = doc.getMetadata().getOrDefault("pageNumber", "N/A");
                    return String.format("[Score: %s | Page: %s]\n%s", fileName, page, doc.getText());
                })
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
