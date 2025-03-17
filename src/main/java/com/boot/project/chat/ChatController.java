package com.boot.project.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @GetMapping("/chatPage")
    public String enterChatPage(@RequestParam("username") String username, @RequestParam("roomId") int roomId, Model model) {
        // username과 roomId를 모델에 담아서 chat.jsp로 전달
        model.addAttribute("username", username);
        model.addAttribute("roomId", roomId);
        return "/chat/chat";  // chat.jsp로 이동
    }

}
