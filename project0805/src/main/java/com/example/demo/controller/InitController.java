package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class InitController {

    @RequestMapping("/websocket/{name}")
    public String webSocket(@PathVariable String name, Model model) {
        try {
//            log.info("跳转到websocket的页面上");
            model.addAttribute("userID", name);
            return "chatroom_call_ok";
        } catch (Exception e) {
//            log.info("跳转到websocket的页面上发生异常，异常信息是：" + e.getMessage());
            return "error";
        }
    }
    
    @RequestMapping("/chat")
    public String webSocket(Model model) {
        try {
//            log.info("跳转到websocket的页面上");
//            model.addAttribute("username", name);
            return "chat";
        } catch (Exception e) {
//            log.info("跳转到websocket的页面上发生异常，异常信息是：" + e.getMessage());
            return "error";
        }
    }    
}
