package com.bank.investment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/test/")
public class TestController {
    @RequestMapping({ "hello" })
    public String firstPage() {
        return "Hello World";
    }
}
