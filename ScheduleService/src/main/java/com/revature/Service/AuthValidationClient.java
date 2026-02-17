package com.revature.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthValidationClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String authServiceBaseUrl;
    private final String authValidatePath;

    public AuthValidationClient(
            @Value("${auth.service.base-url:http://localhost:8081}") String authServiceBaseUrl,
            @Value("${auth.service.validate-path:/auth}") String authValidatePath
    ) {
        this.authServiceBaseUrl = authServiceBaseUrl;
        this.authValidatePath = authValidatePath;
    }

    public AuthValidationResult validate(String authHeader) {
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(joinUrl(authServiceBaseUrl, authValidatePath))
                            .build(true)
                            .toUri(),
                    HttpMethod.GET,
                    authEntity(authHeader),
                    Map.class
            );

            Map<?, ?> body = response.getBody();
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
        } catch (RestClientResponseException ex) {
            return new AuthValidationResult(false, null);
        }
    }

    private HttpEntity<Void> authEntity(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return new HttpEntity<>(headers);
    }

    private String joinUrl(String base, String path) {
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    private Boolean asBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return Boolean.parseBoolean(s);
        }
        return null;
    }

    private String asString(Object value) {
        if (!(value instanceof String s)) {
            return null;
        }
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public record AuthValidationResult(boolean valid, String privilege) {}
}
