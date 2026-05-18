package com.zayden.chat_service.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.zayden.chat_service.dto.request.IntrospectRequest;
import com.zayden.chat_service.entity.WebSocketSession;
import com.zayden.chat_service.service.IdentityService;
import com.zayden.chat_service.service.WebSocketSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;
    WebSocketSessionService webSocketSessionService;
    IdentityService identityService;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");

        var introspectResponse = identityService.introspect(IntrospectRequest.builder().token(token).build());

        if (introspectResponse.isValid()) {
            log.info("Clent connected: {}", client.getSessionId());

            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(introspectResponse.getUserId())
                    .build();
            webSocketSession = webSocketSessionService.create(webSocketSession);
            log.info("WebSocketSession created with id: {}", webSocketSession.getId());
        } else {
            log.info("Authentication failed: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started!");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stopped!");
    }
}
