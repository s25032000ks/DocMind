package com.ai.docMind.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ChatClient chatClient;

    public String greet(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
