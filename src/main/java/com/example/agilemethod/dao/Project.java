package com.example.agilemethod.dao;

import lombok.Data;

import java.util.Date;

@Data
public class Project {

    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
}
