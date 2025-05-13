package com.crewmeister.cmcodingchallenge.currency.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/api/currencies")
    public String redirectToSwaggerUI() {
        return "redirect:/swagger-ui.html";
    }
}