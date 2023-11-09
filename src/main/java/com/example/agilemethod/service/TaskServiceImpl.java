package com.example.agilemethod.service;

import com.example.agilemethod.dao.Task;
import com.example.agilemethod.dto.TaskDTO;
import com.example.agilemethod.dto.request.TaskReqDTO;
import com.example.agilemethod.mapper.TaskMapper;
import com.example.agilemethod.util.AgileUtils;
import com.example.agilemethod.util.Constants;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskMapper taskMapper;

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
}
