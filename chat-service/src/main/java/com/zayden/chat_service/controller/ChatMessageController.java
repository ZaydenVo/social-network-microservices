package com.zayden.chat_service.controller;

import com.zayden.chat_service.dto.ApiResponse;
import com.zayden.chat_service.dto.request.ChatMessageRequest;
import com.zayden.chat_service.dto.response.ChatMessageResponse;
import com.zayden.chat_service.service.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("message")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> createChatMessage(@RequestBody ChatMessageRequest request) {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.createMessage(request))
                .build();
    }

    @GetMapping("/{conversationId}")
    ApiResponse<List<ChatMessageResponse>> getChatMessage(@PathVariable String conversationId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatMessageService.getChatMessage(conversationId))
                .build();
    }
}
