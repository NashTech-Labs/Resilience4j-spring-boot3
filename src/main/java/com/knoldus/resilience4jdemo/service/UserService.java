package com.knoldus.resilience4jdemo.service;

import com.knoldus.resilience4jdemo.model.User;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private static final String SERVICE_NAME = "user-service";
    static  int i =0;
    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "usersFallback")
    @Retry(name = SERVICE_NAME, fallbackMethod = "retryUsersFallback")
    @Bulkhead(name = SERVICE_NAME, fallbackMethod = "bulkheadUsersFallback")
    @RateLimiter(name = SERVICE_NAME, fallbackMethod = "usersFallback")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = restTemplate.getForObject("https://gorest.co.in/public/v2/users", ArrayList.class);
        return new  ResponseEntity<>(users, HttpStatus.OK);
    }

    @TimeLimiter(name = SERVICE_NAME, fallbackMethod = "timiLimiterUsersFallback")
    public CompletableFuture<List<User>> getUsersTimeLimiter() {
        List<User> users = restTemplate.getForObject("https://gorest.co.in/public/v2/users", ArrayList.class);
        return CompletableFuture.completedFuture(users);
    }

    public ResponseEntity<List<User>> usersFallback(Exception ex){
        List<User> users = Arrays.asList(
                new User(1, "name", "Service is down", "email", "gender"),
                new User(1, "name","Service is down", "email", "gender")
        );
        return new ResponseEntity<>(users, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<User>> retryUsersFallback(Exception ex){
        List<User> users = Arrays.asList(
                new User(1, "retryFallback", "Service is down", "email", "gender"),
                new User(1, "retryFallback","Service is down", "email", "gender")
        );
        return new ResponseEntity<>(users, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<User>> bulkheadUsersFallback(Exception ex){
        List<User> users = Arrays.asList(
                new User(1, "bulkheadfallback", "Service is down", "email", "gender"),
                new User(1, "bulkheadfallback","Service is down", "email", "gender")
        );
        return new ResponseEntity<>(users, HttpStatus.TOO_MANY_REQUESTS);
    }

    public CompletableFuture<List<User>> timiLimiterUsersFallback(Exception ex){
        System.out.println("TIme out");
        List<User> users = Arrays.asList(
                new User(1, "Time out", "Service is down", "email", "gender"),
                new User(1, "Time out","Service is down", "email", "gender")
        );
        return CompletableFuture.completedFuture(users);
    }
}
