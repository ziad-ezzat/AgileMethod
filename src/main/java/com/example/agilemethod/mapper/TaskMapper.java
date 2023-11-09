package com.example.agilemethod.mapper;

import com.example.agilemethod.dao.Task;
import com.example.agilemethod.dto.TaskDTO;
import com.example.agilemethod.dto.request.TaskReqDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDTO toTaskDTO(TaskReqDTO taskReqDTO);

    Task toTaskO(TaskDTO taskDTO);
}
