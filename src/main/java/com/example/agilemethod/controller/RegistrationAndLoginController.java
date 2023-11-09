package com.example.agilemethod.controller;

import com.example.agilemethod.dto.request.UserReqDTO;
import com.example.agilemethod.service.RegistrationAndLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/registration")
@RestController
public class RegistrationAndLoginController {

    @Autowired
    private RegistrationAndLoginService registrationService;

    @PostMapping
    public String register(@RequestBody UserReqDTO userReqDTO) throws Exception {
        return registrationService.registerUser(userReqDTO);
    }

    @GetMapping("/sign-in")
    public String signIn(@RequestParam String email, @RequestParam String password) {
        return registrationService.signInUser(email, password);
    }
}