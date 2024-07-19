package com.atguigu.gmall.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class LoginController {

    @GetMapping(value = "/login.html")
    public String login(@RequestParam(value = "originUrl") String originUrl , Model model) {
        log.info("LoginController...login方法执行了....");
        model.addAttribute("originUrl" , originUrl) ;
        return "login" ;
    }

}
