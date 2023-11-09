package com.example.agilemethod.service;

import com.example.agilemethod.dto.request.ProjectReqDTO;

import java.util.concurrent.ExecutionException;

public interface ProjectService {

    String createProject(ProjectReqDTO projectReqDTO) throws ExecutionException, InterruptedException;

    String DeleteProject(String id) throws ExecutionException, InterruptedException;

    String addUserToProject(String projectId, String email) throws ExecutionException, InterruptedException;

    String removeUserFromProject(String projectId, String email) throws ExecutionException, InterruptedException;
}
