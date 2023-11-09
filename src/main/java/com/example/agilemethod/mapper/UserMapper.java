package com.example.agilemethod.mapper;

import com.example.agilemethod.dao.User;
import com.example.agilemethod.dto.UserDTO;
import com.example.agilemethod.dto.request.UserReqDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(UserReqDTO userReqDTO);

    User toUser(UserDTO userDTO);
}
