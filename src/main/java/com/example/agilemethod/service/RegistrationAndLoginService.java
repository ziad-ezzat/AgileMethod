package com.example.agilemethod.service;

import com.example.agilemethod.dto.request.UserReqDTO;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.concurrent.ExecutionException;

public interface RegistrationAndLoginService {

    String registerUser(UserReqDTO userReqDTO) throws ExecutionException, InterruptedException, FirebaseAuthException;
    String signInUser(String email, String password);
}
