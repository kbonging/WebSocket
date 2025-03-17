<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>웹소켓 채팅</title>
    <script>
        let ws;
        // EL 표현식으로 서버에서 전달된 username과 roomId 값을 JavaScript 변수에 저장
        let chatRoomId = ${roomId};  // chat.jsp에서 전달된 roomId 값
        let username = "${username}";  // chat.jsp에서 전달된 username 값

        function connect() {
            ws = new WebSocket("ws://localhost:8090/chat");

            ws.onopen = function() {
                console.log("웹소켓 연결됨");
                //appendMessage(JSON.stringify({ message: "서버와 연결되었습니다." }));

                // 입장 메시지 전송
                let joinMessage = {
                    messageType: "JOIN",
                    chatRoomId: chatRoomId,
                    sender: username
                };
                ws.send(JSON.stringify(joinMessage));
            };

            ws.onmessage = function(event) {
                console.log("메시지 수신: " + event.data);
                appendMessage(event.data);
            };

            ws.onclose = function() {
                console.log("웹소켓 연결 종료");
                appendMessage(JSON.stringify({ message: "서버와의 연결이 종료되었습니다." }));
            };
        }

        function sendMessage() {
            let messageInput = document.getElementById("messageInput");
            let message = messageInput.value.trim();

            if (message === "") return;

            let chatMessage = {
                messageType: "TALK",
                chatRoomId: chatRoomId,
                sender: username,
                message: message
            };

            ws.send(JSON.stringify(chatMessage));
            messageInput.value = "";
        }

        function appendMessage(message) {
            //console.log("message :" + message);
            let jsonMessage = JSON.parse(message);
            let result = "";

            if(jsonMessage.messageType == "JOIN"){
                result = jsonMessage.sender + "님이 입장하셨습니다";
            }else if(jsonMessage.messageType == "TALK"){
                result = jsonMessage.sender + " : " + jsonMessage.message;
            }
            let chatBox = document.getElementById("chatBox");
            let newMessage = document.createElement("p");
            newMessage.textContent = result;
            chatBox.appendChild(newMessage);
        }

        window.onload = connect;
    </script>
</head>
<body>
    <h2>웹소켓 채팅방</h2>
    <div id="chatBox" style="width: 400px; height: 300px; border: 1px solid black; overflow-y: auto; padding: 10px;">
    </div>
    <input type="text" id="messageInput" placeholder="메시지를 입력하세요">
    <button onclick="sendMessage()">전송</button>
</body>
</html>