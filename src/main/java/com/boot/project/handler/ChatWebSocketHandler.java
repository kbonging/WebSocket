package com.boot.project.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.HashMap;
import java.util.Map;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> chatRoomSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("새로운 사용자 접속: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 내용은 JSON 형식이므로 파싱
        String payload = message.getPayload();
        System.out.println("서버에서 받은 메시지: " + payload);  // 받은 메시지 출력

        // 클라이언트가 보낸 메시지에 채팅방 ID가 포함된 경우 처리
        if (payload.contains("chatRoomId")) {
            String chatRoomId = payload.split(":")[1].replaceAll("[^a-zA-Z0-9]", ""); // 예시: 'chatRoomId' 파싱
            if (chatRoomId != null && !chatRoomId.isEmpty()) {
                session.getAttributes().put("chatRoomId", chatRoomId);
                System.out.println("채팅방 ID 설정됨: " + chatRoomId);
            }
        }

        // 채팅방 ID를 세션에서 가져옵니다.
        String chatRoomId = (String) session.getAttributes().get("chatRoomId");

        if (chatRoomId == null) {
            System.out.println("채팅방 ID가 설정되지 않았습니다.");
            return;
        }

        // 채팅방에 연결된 모든 사용자에게 메시지 전송
        for (Map.Entry<String, WebSocketSession> entry : chatRoomSessions.entrySet()) {
            if (entry.getKey().equals(chatRoomId) && entry.getValue().isOpen()) {
                entry.getValue().sendMessage(new TextMessage("새 메시지: " + message.getPayload()));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 사용자가 연결 종료시 채팅방에서 세션 제거
        String chatRoomId = (String) session.getAttributes().get("chatRoomId");
        if (chatRoomId != null) {
            chatRoomSessions.remove(chatRoomId);
            System.out.println("사용자 연결 종료: " + session.getId() + " (채팅방: " + chatRoomId + ")");
        }
    }
}
