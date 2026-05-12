package com.zayden.chat_service.mapper;

import com.zayden.chat_service.dto.response.UserProfileResponse;
import com.zayden.chat_service.entity.ParticipantInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfoMapper {
    ParticipantInfo toParticipantInfo(UserProfileResponse userProfileResponse);
}
