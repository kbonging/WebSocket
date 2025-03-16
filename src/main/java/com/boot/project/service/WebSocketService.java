package com.boot.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String username, WebSocketSession session) {
        sessionMap.put(username, session);
    }

    public WebSocketSession getSession(String username) {
        return sessionMap.get(username);
    }

    public void removeSession(String username) {
        sessionMap.remove(username);
    }

    // 특정 사용자에게 메시지 보내기
    public void sendMessageToUser(String username, String message) throws Exception {
        WebSocketSession session = sessionMap.get(username);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
