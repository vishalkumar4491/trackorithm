package com.trackorithm.track.modules.sheet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sheet")
public class Sample {

    @GetMapping("/sample")
    public String sample() {
        return "Hello, World!";
    }
}
