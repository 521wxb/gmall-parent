package com.atguigu.gmall.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/product")
public class HelloController {

    @GetMapping(value = "/hello")
    public String hello() {
        return "ok" ;
    }

}
