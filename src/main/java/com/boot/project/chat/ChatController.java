package com.boot.project.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chatPage")
    public String chat(){
        return "/chat/chat";
    }

}
