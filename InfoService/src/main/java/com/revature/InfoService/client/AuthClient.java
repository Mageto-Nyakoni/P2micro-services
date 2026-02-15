package com.revature.InfoService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.revature.InfoService.dto.response.AuthResponse;

@FeignClient(name = "auth-service", path = "auth")
public interface AuthClient {
    @GetMapping
    AuthResponse validateToken(@RequestHeader("Authorization") String authHeader);
}