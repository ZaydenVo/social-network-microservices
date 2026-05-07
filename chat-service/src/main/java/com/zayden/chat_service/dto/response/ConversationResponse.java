package com.zayden.chat_service.dto.response;

import com.zayden.chat_service.entity.ParticipantInfo;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String type;
    String participantsHash;
    String conversationName;
    String conversationAvatar;
    List<ParticipantInfo> participants;
    Instant createdDate;
    Instant modifiedDate;
}
