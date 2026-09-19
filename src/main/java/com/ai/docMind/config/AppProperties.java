package com.ai.docMind.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private RagProperties rag = new RagProperties();
    private CorsProperties cors = new CorsProperties();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RagProperties {
        private int chunkSize = 600;
        private  int minChunkSizeChars=350;
        private  int minChunkLengthToEmbed=5;
        private  int maxNumChunks=10000;
        //private int chunkOverlap = 100;
        private int topK = 5;
        private double similarityThreshold = 0.0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorsProperties {
        private String allowedOrigins = "*";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
    }
}
