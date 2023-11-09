package com.example.agilemethod.dao;

import com.example.agilemethod.model.enums.TaskStatus;
import lombok.Data;

@Data
public class Task {

    private String id;
    private String name;
    private String description;
    private TaskStatus status;
}
