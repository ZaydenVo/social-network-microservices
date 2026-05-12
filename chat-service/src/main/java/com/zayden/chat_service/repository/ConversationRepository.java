package com.zayden.chat_service.repository;

import com.zayden.chat_service.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Optional<Conversation> findByParticipantsHash(String participantsHash);
}
