package com.zayden.chat_service.service;

import com.zayden.chat_service.dto.request.ChatMessageRequest;
import com.zayden.chat_service.dto.response.ChatMessageResponse;
import com.zayden.chat_service.entity.ChatMessage;
import com.zayden.chat_service.exception.AppException;
import com.zayden.chat_service.exception.ErrorCode;
import com.zayden.chat_service.mapper.ChatMessageMapper;
import com.zayden.chat_service.mapper.InfoMapper;
import com.zayden.chat_service.repository.ChatMessageRepository;
import com.zayden.chat_service.repository.ConversationRepository;
import com.zayden.chat_service.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    ProfileClient profileClient;
    InfoMapper infoMapper;
    ChatMessageMapper chatMessageMapper;

    public ChatMessageResponse createMessage(ChatMessageRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var conversation = conversationRepository.findById(request.getConversationId()).orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        conversation.getParticipants().stream()
                .filter(participantInfo -> participantInfo.getUserId().equals(userId))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        var userInfo = profileClient.getUserProfile(userId).getResult();
        if (Objects.isNull(userInfo)) throw new AppException(ErrorCode.PROFILE_NOT_FOUND);

        ChatMessage chatMessage = ChatMessage.builder()
                .conversationId(request.getConversationId())
                .message(request.getMessage())
                .sender(infoMapper.toParticipantInfo(userInfo))
                .createdDate(Instant.now())
                .build();

        return toChatMessageResponse(chatMessageRepository.save(chatMessage), userId);
    }

    public List<ChatMessageResponse> getChatMessage(String conversationId) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        return chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId)
                .stream()
                .map(chatMessage -> toChatMessageResponse(chatMessage, userId))
                .toList();
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage, String currentUserId) {
        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);

        chatMessageResponse.setMe(chatMessage.getSender().getUserId().equals(currentUserId));

        return chatMessageResponse;
    }
}
