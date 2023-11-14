package com.example.agilemethod.service;

import com.example.agilemethod.dao.Project;
import com.example.agilemethod.dao.Sprint;
import com.example.agilemethod.dao.User;
import com.example.agilemethod.dto.request.ProjectReqDTO;
import com.example.agilemethod.dto.request.SprintReqDTO;
import com.example.agilemethod.mapper.ProjectMapper;
import com.example.agilemethod.mapper.SprintMapper;
import com.example.agilemethod.util.AgileUtils;
import com.example.agilemethod.util.Constants;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public String createProject(ProjectReqDTO projectReqDTO) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        Project project = projectMapper.toProject(projectReqDTO);
        String id = AgileUtils.generateId(Constants.ProjectTable);
        project.setId(id);
        db.collection(Constants.ProjectTable).document(id).set(project);
        return "Project created successfully";
    }

    @Override
    public String DeleteProject(String id) throws ExecutionException, InterruptedException {

        if (!AgileUtils.isExistInTable(Constants.ProjectTable, id)) {
            return "Project not found";
        }

        Firestore db = FirestoreClient.getFirestore();
        db.collection(Constants.ProjectTable).document(id).delete();
        return "Project deleted successfully";
    }

    @Override
    public String addUserToProject(String projectId, String email) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> future = db.collection(Constants.UserTable).document(email).get();
        DocumentSnapshot document = future.get();
        User user = null;
        if (document.exists()) {
            user = document.toObject(User.class);
        } else {
            return "User not found";
        }

        UserRecord userRecord;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(user.getId());
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return "User not found";
        }

        if (!userRecord.isEmailVerified()) {
            return "User email not verified";
        }

        if (!AgileUtils.isExistInTable(Constants.ProjectTable, projectId)) {
            return "Project not found";
        }

        db.collection(Constants.ProjectTable).document(projectId).collection("users").document(user.getEmail()).set(user);

        Project project = db.collection(Constants.ProjectTable).document(projectId).get().get().toObject(Project.class);

        sendEmail(user.getEmail(), "You have been added to a project", "You have been added to a project " + project.getName() + " Check it out Now!");

        return "User added to project successfully";
    }

    @Override
    public String removeUserFromProject(String projectId, String email) throws ExecutionException, InterruptedException {

            if (!AgileUtils.isExistInTable(Constants.ProjectTable, projectId)) {
                return "Project not found";
            }

            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<DocumentSnapshot> future = db.collection(Constants.UserTable).document(email).get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
            } else {
                return "User not found";
            }

            db.collection(Constants.ProjectTable).document(projectId).collection("users").document(email).delete();

            Project project = db.collection(Constants.ProjectTable).document(projectId).get().get().toObject(Project.class);

            sendEmail(email, "You have been removed from a project", "You have been removed from a project " + project.getName() + " Check it out Now!");

            return "User removed from project successfully";
    }

    @Override
    public String createSprint(String projectId, SprintReqDTO sprintReqDTO) throws ExecutionException, InterruptedException {

        if (!AgileUtils.isExistInTable(Constants.ProjectTable, projectId)) {
            return "Project not found";
        }

        Firestore db = FirestoreClient.getFirestore();
        String id = AgileUtils.generateId(Constants.SprintTable);
        Sprint sprint = sprintMapper.toSprint(sprintReqDTO);
        sprint.setId(id);
        sprint.setProjectId(projectId);
        db.collection(Constants.SprintTable).document(id).set(sprint);
        return "Sprint created successfully";
    }

    @Override
    public String deleteSprint(String sprintId) throws ExecutionException, InterruptedException {

        if (!AgileUtils.isExistInTable(Constants.SprintTable, sprintId)) {
            return "Sprint not found";
        }

        Firestore db = FirestoreClient.getFirestore();
        db.collection(Constants.SprintTable).document(sprintId).delete();
        return "Sprint deleted successfully";
    }

    private void sendEmail(String to, String subject, String body) {
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
