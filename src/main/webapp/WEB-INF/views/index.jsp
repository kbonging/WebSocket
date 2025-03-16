<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Chat</title>
    <script>
        let ws;
        let userName;

        function connectWebSocket() {
            userName = document.getElementById("userName").value.trim();
            if (!userName) {
                alert("이름을 입력하세요!");
                return;
            }

            ws = new WebSocket("ws://localhost:8090/chat?userName=" + encodeURIComponent(userName));

            ws.onopen = function() {
                console.log("웹소켓 연결됨");
                document.getElementById("status").innerText = "연결됨";
            };

            ws.onmessage = function(event) {
                const chatBox = document.getElementById("chatBox");
                const newMessage = document.createElement("p");
                newMessage.innerText = event.data;
                chatBox.appendChild(newMessage);
            };

            ws.onclose = function() {
                console.log("웹소켓 종료됨");
                document.getElementById("status").innerText = "연결 종료";
            };

            ws.onerror = function(error) {
                console.error("웹소켓 오류", error);
            };
        }

        function sendMessage() {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                alert("웹소켓이 연결되지 않았습니다.");
                return;
            }
            let message = document.getElementById("message").value.trim();
            if (message) {
                ws.send(message);
                document.getElementById("message").value = "";
            }
        }
    </script>
</head>
<body>
    <h2>웹소켓 채팅</h2>
    <div>
        <label>이름: <input type="text" id="userName"></label>
        <button onclick="connectWebSocket()">채팅 시작</button>
        <p id="status">연결되지 않음</p>
    </div>

    <div id="chatBox" style="border:1px solid #000; width:300px; height:200px; overflow:auto;"></div>

    <div>
        <input type="text" id="message" placeholder="메시지를 입력하세요">
        <button onclick="sendMessage()">전송</button>
    </div>
</body>
</html>
