package com.revature.ScheduleService.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "${clients.auth.service-name:AuthService}",
        contextId = "authValidationClient",
        path = "${clients.auth.path:/smart-appointment/api/auth}"
)
public interface AuthValidationClient {

    @GetMapping
    Map<String, Object> validateToken(@RequestHeader("Authorization") String authHeader);
}
