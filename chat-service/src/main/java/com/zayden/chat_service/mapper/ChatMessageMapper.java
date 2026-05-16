package com.zayden.chat_service.mapper;

import com.zayden.chat_service.dto.response.ChatMessageResponse;
import com.zayden.chat_service.entity.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);
}
