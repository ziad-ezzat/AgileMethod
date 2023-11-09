package com.example.agilemethod.util;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.concurrent.ExecutionException;

public class AgileUtils {


    public static boolean isExistInTable(String table, String code) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentSnapshot ownerDoc = dbFirestore.collection(table).document(code).get().get();
        return ownerDoc.exists();
    }

    public static String generateId(String table) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        String id = dbFirestore.collection(table).document().getId();
        return id;
    }

    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public static boolean isUserEmailExists(String email) {
        try {
            FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
                return false;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
