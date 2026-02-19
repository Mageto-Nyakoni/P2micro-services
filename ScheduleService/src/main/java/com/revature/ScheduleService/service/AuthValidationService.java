package com.revature.ScheduleService.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.revature.ScheduleService.client.AuthValidationClient;

import feign.FeignException;

@Service
public class AuthValidationService {

    private final AuthValidationClient authValidationClient;

    public AuthValidationService(AuthValidationClient authValidationClient) {
        this.authValidationClient = authValidationClient;
    }

    public AuthValidationResult validate(String authHeader) {
        try {
            Map<String, Object> body = authValidationClient.validateToken(authHeader);
            if (body == null) {
                return new AuthValidationResult(false, null);
            }

            Boolean valid = asBoolean(body.get("valid"));
            if (valid == null) {
                valid = asBoolean(body.get("isValid"));
            }

            String privilege = asString(body.get("privilege"));
            if (privilege == null) {
                privilege = asString(body.get("role"));
            }

            return new AuthValidationResult(Boolean.TRUE.equals(valid), privilege);
        } catch (FeignException ex) {
            return new AuthValidationResult(false, null);
        }
    }

    public void validateAdmin(String authHeader) {
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        AuthValidationResult validation = validate(authHeader);
        if (!validation.valid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String privilege = validation.privilege();
        if (!"Admin".equals(privilege) && !"Super".equals(privilege)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    private Boolean asBoolean(Object value) {
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof String stringValue) {
            return Boolean.parseBoolean(stringValue);
        }
        return null;
    }

    private String asString(Object value) {
        if (!(value instanceof String stringValue)) {
            return null;
        }
        String trimmed = stringValue.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public record AuthValidationResult(boolean valid, String privilege) {}
}
