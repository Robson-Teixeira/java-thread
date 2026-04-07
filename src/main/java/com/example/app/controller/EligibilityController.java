package com.example.app.controller;

import com.example.app.model.Eligibility;
import com.example.app.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EligibilityController {

    private final EligibilityService eligibilityService;

    @GetMapping("/test1")
    public Eligibility runMethod1(@RequestHeader(value = "x-type-request", required = false) String typeRequest) {
        Eligibility eligibility = eligibilityService.method1(typeRequest);
        log.info("Executado method1! Veja log.");
        return eligibility;
    }

    @GetMapping("/test2")
    public Eligibility runMethod2(@RequestHeader(value = "x-type-request", required = false) String typeRequest) throws Exception {
        Eligibility eligibility = eligibilityService.method2(typeRequest);
        log.info("Executado method2! Veja log.");
        return eligibility;
    }
}