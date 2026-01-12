package com.revature.smartAppointment.Controller.Response;

import com.revature.smartAppointment.Model.Privilege;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String email;
    private Privilege privilege;

    private String token;
}
