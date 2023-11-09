package com.example.agilemethod.controller;

import com.example.agilemethod.dto.TaskDTO;
import com.example.agilemethod.dto.request.TaskReqDTO;
import com.example.agilemethod.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public TaskDTO createTask(@RequestBody TaskReqDTO taskReqDTO) throws Exception {
        return taskService.createTask(taskReqDTO);
    }

    @DeleteMapping("/delete")
    public String deleteTask(@RequestParam String id) {
        return taskService.deleteTask(id);
    }
}
