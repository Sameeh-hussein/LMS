package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRequestMapper implements Mapper<User, SignupRequest> {
    private final ModelMapper modelMapper;

    @Override
    public SignupRequest mapTo(User user) {
        return modelMapper.map(user, SignupRequest.class);
    }

    @Override
    public User mapFrom(SignupRequest request) {
        return modelMapper.map(request, User.class);
    }
}
