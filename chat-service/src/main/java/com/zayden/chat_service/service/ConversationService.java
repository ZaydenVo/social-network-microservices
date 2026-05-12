package com.zayden.chat_service.service;

import com.zayden.chat_service.dto.request.ConversationRequest;
import com.zayden.chat_service.dto.response.ConversationResponse;
import com.zayden.chat_service.entity.Conversation;
import com.zayden.chat_service.entity.ParticipantInfo;
import com.zayden.chat_service.exception.AppException;
import com.zayden.chat_service.exception.ErrorCode;
import com.zayden.chat_service.mapper.ConversationMapper;
import com.zayden.chat_service.mapper.InfoMapper;
import com.zayden.chat_service.repository.ConversationRepository;
import com.zayden.chat_service.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;
    InfoMapper infoMapper;
    ConversationMapper conversationMapper;

    public ConversationResponse createConversation(ConversationRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var userInfo = profileClient.getUserProfile(userId).getResult();
        var participantInfo = profileClient.getUserProfile(request.getParticipantIds().getFirst()).getResult();

        if (Objects.isNull(userInfo) || Objects.isNull(participantInfo))
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfo.getUserId());
        userIds.sort(null);

        String userIdsHash = generateParticipantsHash(userIds);

        var conversation = conversationRepository.findByParticipantsHash(userIdsHash)
                .orElseGet(() -> {
                    List<ParticipantInfo> participantInfos = List.of(
                            infoMapper.toParticipantInfo(userInfo), infoMapper.toParticipantInfo(participantInfo)
                    );
                    Conversation newConversation = Conversation.builder()
                            .type(request.getType())
                            .participantsHash(userIdsHash)
                            .createdDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .conversationName(participantInfo.getUsername())
                            .conversationAvatar(participantInfo.getAvatar())
                            .build();
                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation);
    }

    public List<ConversationResponse> getMyConversations() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var conversationLists = conversationRepository.findAllByParticipantIdsContains(userId);

        return conversationLists.stream().map(this::toConversationResponse).toList();
    }

    public String deleteConversation(String conversationId) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        if (conversation.getParticipants().stream().noneMatch(participantInfo -> participantInfo.getUserId().equals(userId)))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        conversationRepository.delete(conversation);

        return "Conversation has been deleted!";
    }

    private String generateParticipantsHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(userId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });

        return conversationResponse;
    }
}
