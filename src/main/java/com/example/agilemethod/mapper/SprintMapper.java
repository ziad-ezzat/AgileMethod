package com.example.agilemethod.mapper;

import com.example.agilemethod.dao.Sprint;
import com.example.agilemethod.dto.request.SprintReqDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SprintMapper {

    Sprint toSprint(SprintReqDTO sprintReqDTO);
}
