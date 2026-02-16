package com.revature.InfoService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {
    private Integer privilegeId;
    private String roleName;

    public Privilege(String roleName) {
        this.roleName = roleName;
    }
}