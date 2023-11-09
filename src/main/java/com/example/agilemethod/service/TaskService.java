package com.example.agilemethod.service;

import com.example.agilemethod.dto.TaskDTO;
import com.example.agilemethod.dto.request.TaskReqDTO;

import java.util.concurrent.ExecutionException;

public interface TaskService {

    TaskDTO createTask(TaskReqDTO taskReqDTO) throws ExecutionException, InterruptedException;
    String deleteTask(String id);
}
