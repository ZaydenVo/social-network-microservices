package com.zayden.chat_service.controller;

import com.zayden.chat_service.dto.ApiResponse;
import com.zayden.chat_service.dto.request.ConversationRequest;
import com.zayden.chat_service.dto.response.ConversationResponse;
import com.zayden.chat_service.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
