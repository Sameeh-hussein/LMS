package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserReturnMapper implements Mapper<User, ReturnUserDto> {
    private final ModelMapper modelMapper;

    public UserReturnMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.typeMap(User.class, ReturnUserDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getRole().getName(), ReturnUserDto::setRoleName);
        });
    }

    @Override
    public ReturnUserDto mapTo(User user) {
        return ReturnUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roleName(user.getRole().getName())
                .profileImage(user.getProfileImage().getPath())
                .build();
    }

    @Override
    public User mapFrom(ReturnUserDto returnUserDto) {
        return modelMapper.map(returnUserDto, User.class);
    }
}
