package com.ruoyi.web.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysIndexController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}