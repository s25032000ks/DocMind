package com.ai.docMind.controller;

import com.ai.docMind.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Management Controller",
        description = "Endpoint for doing chat with documents"
)
public class ChatController {

    private final ChatService chatService;

    @PostMapping()
    @Operation(summary = "This is used to greet")
    public ResponseEntity<String> greet(@RequestBody String message){
        return ResponseEntity.ok(chatService.greet(message));
    }
}
