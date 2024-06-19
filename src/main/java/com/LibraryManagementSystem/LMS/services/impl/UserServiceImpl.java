package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.auth.*;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.exceptions.*;
import com.LibraryManagementSystem.LMS.mappers.impl.UserRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.UserReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRequestMapper userRequestMapper;
    private final UserReturnMapper userReturnMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           UserRequestMapper userRequestMapper,
                           UserReturnMapper userReturnMapper
                           ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRequestMapper = userRequestMapper;
        this.userReturnMapper = userReturnMapper;
    }

    @Override
    public String authenticateUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Incorrect password");
        }

        return jwtUtil.generateToken(user);
    }

    @Override
    public void registerUserWithRole(SignupRequest request, String roleName) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email: " + request.getEmail() + " already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username: " + request.getUsername() + " already exists");
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            throw new RoleNotFoundException("Role not found with name: " + roleName);
        }

        User userToAdd = userRequestMapper.mapFrom(request);
        userToAdd.setPassword(passwordEncoder.encode(request.getPassword()));
        userToAdd.setRole(role.get());
        userToAdd.setCreatedAt(LocalDateTime.now());

        userRepository.save(userToAdd);
    }

    @Override
    public List<ReturnUserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnUserDto findUserById(Long id) {
        return userRepository.findUserById(id)
                .map(userReturnMapper::mapTo)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not exist"));
    }

    @Override
    public void updateUserData(Long userId, UpdateDataRequest request) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + "not exist"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password doesn't match");
        }

        user.setEmail(request.getData().getEmail());
        user.setUsername(request.getData().getUsername());
        user.setPassword(passwordEncoder.encode(request.getData().getPassword()));

        userRepository.save(user);
    }
}
