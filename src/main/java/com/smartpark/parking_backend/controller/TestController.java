package com.smartpark.parking_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String sayHello() {
        return "FUCK ASS PROJECYT FUCK ASS FUCK FUCK FUCK DIE DIE DIE";
    }
}