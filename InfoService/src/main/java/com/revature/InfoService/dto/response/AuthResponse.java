package com.revature.InfoService.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String privilege;
    private Integer userId;
    private String token;
    private Boolean valid;
}
