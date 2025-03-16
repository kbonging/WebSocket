<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>웹소켓 채팅</title>
</head>
<body>
    <h1>웹소켓 채팅</h1>
    커밋 테스ㅡ111111111111
    <div>
        <!-- 채팅방 ID 입력 -->
        <input type="text" id="chatRoomId" placeholder="채팅방 ID" />
    </div>

    <div>
        <!-- 메시지 입력 -->
        <input type="text" id="messageInput" placeholder="메시지 입력" />
    </div>

    <div>
        <!-- 메시지 보내기 버튼 -->
        <button onclick="sendMessage()">메시지 보내기</button>
    </div>

    <script>
        let socket;

        // 웹소켓 연결
        function connectToChat() {
            const chatRoomId = document.getElementById("chatRoomId").value;
            if (!chatRoomId) {
                alert("채팅방 ID를 입력하세요!");
                return;
            }

            // 웹소켓 연결
            socket = new WebSocket("ws://localhost:8090/chat");

            socket.onopen = () => {
                console.log("웹소켓 연결 성공!");
                // 채팅방 ID를 서버로 전송 (세션에 저장될 수 있도록)
                socket.send(JSON.stringify({ chatRoomId: chatRoomId }));
            };

            socket.onmessage = (event) => {
                console.log("서버 메시지:", event.data);
            };

            socket.onclose = () => {
                console.log("웹소켓 연결 종료");
            };
        }

        // 메시지 보내기
        function sendMessage() {
            const message = document.getElementById("messageInput").value;
            if (message && socket && socket.readyState === WebSocket.OPEN) {
                socket.send(message); // 메시지를 서버로 전송
                document.getElementById("messageInput").value = ""; // 입력창 초기화
            } else {
                alert("웹소켓 연결이 열리지 않았습니다.");
            }
        }

        // 채팅방 연결
        document.getElementById("chatRoomId").addEventListener("change", connectToChat);
    </script>
</body>
</html>
