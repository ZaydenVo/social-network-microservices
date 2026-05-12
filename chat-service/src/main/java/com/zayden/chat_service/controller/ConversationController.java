package com.zayden.chat_service.controller;

import com.zayden.chat_service.dto.ApiResponse;
import com.zayden.chat_service.dto.request.ConversationRequest;
import com.zayden.chat_service.dto.response.ConversationResponse;
import com.zayden.chat_service.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("conversations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;

    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody ConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.createConversation(request))
                .build();
    }

    @GetMapping("/my-conversations")
    ApiResponse<List<ConversationResponse>> getMyConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(conversationService.getMyConversations())
                .build();
    }

    @DeleteMapping("/delete/{conversationId}")
    ApiResponse<String> deleteConversation(@PathVariable String conversationId) {
        return ApiResponse.<String>builder()
                .result(conversationService.deleteConversation(conversationId))
                .build();
    }
}
