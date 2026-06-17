package com.company.payroll.controller;

import com.company.payroll.util.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FoundationController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("API is running", Map.of(
                "status", "UP",
                "phase", "2-foundation"
        ));
    }
}
