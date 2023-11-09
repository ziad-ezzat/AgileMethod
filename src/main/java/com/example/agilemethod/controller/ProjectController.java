package com.example.agilemethod.controller;

import com.example.agilemethod.dto.request.ProjectReqDTO;
import com.example.agilemethod.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public String creteProject(@RequestBody ProjectReqDTO projectReqDTO) throws Exception {
        return projectService.createProject(projectReqDTO);
    }

    @DeleteMapping("/delete")
    public String deleteProject(String id) throws ExecutionException, InterruptedException {
        return projectService.DeleteProject(id);
    }

    @PostMapping("/addUser")
    public String addUserToProject(String projectId, String email) throws ExecutionException, InterruptedException {
        return projectService.addUserToProject(projectId, email);
    }

    @DeleteMapping("/removeUser")
    public String removeUserFromProject(String projectId, String email) throws ExecutionException, InterruptedException {
        return projectService.removeUserFromProject(projectId, email);
    }
}
