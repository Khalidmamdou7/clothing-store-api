package com.example.clothingstoreapi.controller;

import com.example.clothingstoreapi.entity.dto.UserLoginReqDTO;
import com.example.clothingstoreapi.entity.dto.UserLoginResDTO;
import com.example.clothingstoreapi.entity.dto.UserProfileDTO;
import com.example.clothingstoreapi.security.Util.JwtUtil;
import com.example.clothingstoreapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody UserProfileDTO userRegisterReq){
        return userService.saveUser(userRegisterReq);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginReqDTO userLoginReq) {
        log.info("User: {} trying to login", userLoginReq);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginReq.getEmail(), userLoginReq.getPassword())
            );
        } catch (Exception ex) {
            log.error("Invalid email/password");
            return ResponseEntity.internalServerError().body("Invalid email/password");
        }
        return ResponseEntity.ok().body(new UserLoginResDTO(userLoginReq.getEmail(), jwtUtil.generateToken(userLoginReq.getEmail())));
    }

    // For testing only

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }
}
