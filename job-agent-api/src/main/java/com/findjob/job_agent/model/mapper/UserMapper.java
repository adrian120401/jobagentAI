package com.findjob.job_agent.model.mapper;

import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.model.entity.User;

public class UserMapper {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCv_path(), user.getDocx_path(), user.getResumeProfile());
    }

    public static User toEntity(UserRequestDTO userRequestDTO) {
        return new User(userRequestDTO.getName(), userRequestDTO.getEmail(), userRequestDTO.getPassword());
    }
}
