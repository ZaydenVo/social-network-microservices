package com.zayden.chat_service.mapper;

import com.zayden.chat_service.dto.response.ConversationResponse;
import com.zayden.chat_service.entity.Conversation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);
}
