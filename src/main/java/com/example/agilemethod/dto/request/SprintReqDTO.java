package com.example.agilemethod.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class SprintReqDTO {

    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
}
