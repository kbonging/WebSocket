package com.boot.project.handler;

import com.boot.project.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;

    // 소켓 세션을 저장할 Set
    private final Set<WebSocketSession> sessions = new HashSet<>();

    // 채팅방 id와 소켓 세션을 저장할 Map
    private final Map<Long,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO Auto-generated method stub
        log.info("{} 연결됨", session.getId());
        sessions.add(session);

        // 메시지를 담을 객체
//        Map<String, String> messageData = new HashMap<>();
//        messageData.put("message", "WebSocket 연결 완료");
//        // ObjectMapper를 이용해 객체를 JSON 문자열로 변환
//        String jsonMessage = new ObjectMapper().writeValueAsString(messageData);
//
//        session.sendMessage(new TextMessage(jsonMessage));
    }

    // 소켓 메세지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 클라이언트로부터 받은 메세지를 ChatMessageDto로 변환
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        // 메세지 타입에 따라 분기
        if(chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.JOIN)){
            // 입장 메세지
            chatRoomSessionMap.computeIfAbsent(chatMessageDto.getChatRoomId(), s -> new HashSet<>()).add(session);
            chatMessageDto.setMessage(chatMessageDto.getSender()+"님이 입장하셨습니다.");
        }
        else if(chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.LEAVE)){
            // 퇴장 메세지
            chatRoomSessionMap.get(chatMessageDto.getChatRoomId()).remove(session);
            chatMessageDto.setMessage("님이 퇴장하셨습니다.");
        }

        // 채팅 메세지 전송
        for(WebSocketSession webSocketSession : chatRoomSessionMap.get(chatMessageDto.getChatRoomId())){
            System.out.println("sysout : "+new TextMessage(mapper.writeValueAsString(chatMessageDto)));
            webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessageDto)));
        }
    }

    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // TODO Auto-generated method stub
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
//        session.sendMessage(new TextMessage("WebSocket 연결 종료"));
    }
}
