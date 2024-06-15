package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.auth.*;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.exceptions.*;
import com.LibraryManagementSystem.LMS.mappers.impl.UserMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           UserMapper userMapper)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
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

        User userToAdd = userMapper.mapFrom(request);
        userToAdd.setPassword(passwordEncoder.encode(request.getPassword()));
        userToAdd.setRole(role.get());
        userToAdd.setCreatedAt(LocalDateTime.now());

        userRepository.save(userToAdd);
    }
}
