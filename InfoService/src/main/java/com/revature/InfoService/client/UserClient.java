package com.revature.InfoService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.revature.InfoService.dto.User;

@FeignClient(name = "AuthService", contextId = "userService", path = "/smart-appointment/api/users")
public interface UserClient {
    @GetMapping("/{user_id}")
    User getUser(@PathVariable int user_id);
}
