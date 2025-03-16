package com.boot.project.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // 세션 ID를 key로 하고, 유저 정보를 저장하는 Map
    private static final Map<String, UserInfo> CLIENTS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userName = getUserNameFromSession(session);
        System.out.println(userName + "님이 접속하셨습니다. session.getId() : " + session.getId());

        // 유저 정보를 저장 (세션 ID -> 유저 정보)
        CLIENTS.put(session.getId(), new UserInfo(session, userName));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        UserInfo userInfo = CLIENTS.remove(sessionId);

        if (userInfo != null) {
            System.out.println(userInfo.getUserName() + "님이 퇴장하셨습니다.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UserInfo senderInfo = CLIENTS.get(session.getId());
        if (senderInfo == null) return;

        String userName = senderInfo.getUserName();
        String msgContent = message.getPayload();

        String fullMessage = userName + ": " + msgContent;

        // 모든 클라이언트에게 메시지 전송
        for (UserInfo userInfo : CLIENTS.values()) {
            try {
                userInfo.getSession().sendMessage(new TextMessage(fullMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 클라이언트가 웹소켓 연결 시 userName을 파라미터로 보내도록 설정
    private String getUserNameFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery(); // "userName=홍길동" 형식
        if (query != null && query.startsWith("userName=")) {
            return query.substring(9); // "userName=" 이후 문자열을 추출
        }
        return "익명"; // 기본 이름
    }

    // 사용자 정보 클래스
    private static class UserInfo {
        private final WebSocketSession session;
        private final String userName;

        public UserInfo(WebSocketSession session, String userName) {
            this.session = session;
            this.userName = userName;
        }

        public WebSocketSession getSession() {
            return session;
        }

        public String getUserName() {
            return userName;
        }
    }
}
