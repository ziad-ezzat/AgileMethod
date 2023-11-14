package com.example.agilemethod.service;

import com.example.agilemethod.dao.Task;
import com.example.agilemethod.dto.TaskDTO;
import com.example.agilemethod.dto.request.TaskReqDTO;
import com.example.agilemethod.mapper.TaskMapper;
import com.example.agilemethod.util.AgileUtils;
import com.example.agilemethod.util.Constants;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public TaskDTO createTask(TaskReqDTO taskReqDTO) throws ExecutionException, InterruptedException {

        TaskDTO taskDTO = taskMapper.toTaskDTO(taskReqDTO);
        taskDTO.setStatus("TODO");
        taskDTO.setId(AgileUtils.generateId(Constants.TaskTable));

        Task task = taskMapper.toTaskO(taskDTO);

        Firestore dbFirestore = AgileUtils.getFirestore();
        dbFirestore.collection(Constants.TaskTable).document(taskDTO.getId()).set(task);

        return taskDTO;
    }

    @Override
    public String deleteTask(String id) {

        Firestore dbFirestore = AgileUtils.getFirestore();
        dbFirestore.collection(Constants.TaskTable).document(id).delete();
        return "Task with ID: " + id + " has been deleted!";
    }

    @Override
    public String addTaskToUser(String taskId, String email) throws ExecutionException, InterruptedException {

        if (!AgileUtils.isExistInTable(Constants.UserTable, email)) {
            return "User with email: " + email + " not found";
        }

        if (!AgileUtils.isExistInTable(Constants.TaskTable, taskId)) {
            return "Task with ID: " + taskId + " not found";
        }

        Firestore dbFirestore = AgileUtils.getFirestore();
        Task task = dbFirestore.collection(Constants.TaskTable).document(taskId).get().get().toObject(Task.class);
        task.setUserId(email);
        dbFirestore.collection(Constants.TaskTable).document(taskId).set(task);

        sendEmail(email, "Task Assigned", "You have been assigned a task with ID: " + task.getName() + "Check it out!");

        return "Task with ID: " + taskId + " has been assigned to user with email: " + email;
    }

    @Override
    public String removeTaskFromUser(String taskId) throws ExecutionException, InterruptedException {

        if (!AgileUtils.isExistInTable(Constants.TaskTable, taskId)) {
            return "Task with ID: " + taskId + " not found";
        }
        Firestore dbFirestore = AgileUtils.getFirestore();
        Task task = dbFirestore.collection(Constants.TaskTable).document(taskId).get().get().toObject(Task.class);
        task.setUserId(null);
        dbFirestore.collection(Constants.TaskTable).document(taskId).set(task);

        sendEmail(task.getUserId(), "Task Removed", "You have been removed from a task with ID: " + task.getName() + "Check it out!");

        return "Task with ID: " + taskId + " has been removed from user";
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
