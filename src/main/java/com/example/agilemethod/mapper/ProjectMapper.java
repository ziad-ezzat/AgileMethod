package com.example.agilemethod.mapper;

import com.example.agilemethod.dao.Project;
import com.example.agilemethod.dto.request.ProjectReqDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toProject(ProjectReqDTO projectReqDTO);
}
