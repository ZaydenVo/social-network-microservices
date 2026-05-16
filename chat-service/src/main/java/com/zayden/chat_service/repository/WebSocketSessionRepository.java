package com.zayden.chat_service.repository;

import com.zayden.chat_service.entity.WebSocketSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketSessionRepository extends MongoRepository<WebSocketSession, String> {
}
