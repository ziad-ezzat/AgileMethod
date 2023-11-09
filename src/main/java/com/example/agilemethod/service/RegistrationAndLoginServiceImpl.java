package com.example.agilemethod.service;

import com.example.agilemethod.dao.User;
import com.example.agilemethod.dto.UserDTO;
import com.example.agilemethod.dto.request.UserReqDTO;
import com.example.agilemethod.mapper.UserMapper;
import com.example.agilemethod.util.AgileUtils;
import com.example.agilemethod.util.Constants;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class RegistrationAndLoginServiceImpl implements RegistrationAndLoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public String registerUser(UserReqDTO userReqDTO) throws FirebaseAuthException, ExecutionException, InterruptedException {

        boolean isUserEmailExists = AgileUtils.isUserEmailExists(userReqDTO.getEmail());

        if (isUserEmailExists) {
            return "User with email: " + userReqDTO.getEmail() + " already exists!";
        }

        UserDTO userDTO = userMapper.toUserDTO(userReqDTO);

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userDTO.getEmail())
                .setEmailVerified(false)
                .setPassword(userDTO.getPassword())
                .setDisplayName(userDTO.getName())
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        String emailVerificationLink = FirebaseAuth.getInstance().generateEmailVerificationLink(userDTO.getEmail());

        User user = userMapper.toUser(userDTO);

        user.setId(userRecord.getUid());
        user.setSignedIn(false);

        Firestore dbFirestore = AgileUtils.getFirestore();
        dbFirestore.collection(Constants.UserTable).document(user.getEmail()).set(user);

        sendEmail(userDTO.getEmail(), "Verify your account", emailVerificationLink);

        return "send email to " + userDTO.getEmail() + " to verify account " + emailVerificationLink;
    }

    @Override
    public String signInUser(String email, String password) {

        if (!AgileUtils.isUserEmailExists(email)) {
            return "User with email: " + email + " does not exist!";
        }

        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            if (userRecord.isDisabled()) {
                return "User with email: " + email + " is disabled!";
            }
            if (!userRecord.isEmailVerified()) {
                return "User with email: " + email + " is not verified!";
            }
            Firestore dbFirestore = AgileUtils.getFirestore();
            User user = dbFirestore.collection(Constants.UserTable).document(userRecord.getEmail()).get().get().toObject(User.class);
            if (user.isSignedIn()) {
                return "User with email: " + email + " is already signed in!";
            }
            if (!user.getPassword().equals(password)) {
                return "User with email: " + email + " has wrong password!";
            }
            user.setSignedIn(true);
            dbFirestore.collection(Constants.UserTable).document(userRecord.getEmail()).set(user);
            return "User with email: " + email + " is signed in!";
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return "User with email: " + email + " does not exist!";
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}