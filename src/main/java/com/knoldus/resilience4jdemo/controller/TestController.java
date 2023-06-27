package com.knoldus.resilience4jdemo.controller;

import com.knoldus.resilience4jdemo.model.User;
import com.knoldus.resilience4jdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class TestController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/timelimiter")
    public CompletableFuture<List<User>> getUsersTimeLimiter() {
        return userService.getUsersTimeLimiter();
    }
}
