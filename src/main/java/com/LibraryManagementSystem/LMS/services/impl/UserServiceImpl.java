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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRequestMapper userRequestMapper;
    private final UserReturnMapper userReturnMapper;
    private final AuthenticationManager authenticationManager;

    @Override
    public String authenticateUser(@NotNull LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (InternalAuthenticationServiceException e) {
            throw new UserNotFoundException("User not found with email: " + request.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Password does not match");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + request.getEmail()));

        return jwtUtil.generateToken(user);
    }

    @Override
    public void registerUserWithRole(@NotNull SignupRequest request, String roleName) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email: " + request.getEmail() + " already exists");
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            throw new RoleNotFoundException("Role not found with name: " + roleName);
        }

        User user = userRequestMapper.mapFrom(request);
        user.setRole(role.get());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public List<ReturnUserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnUserDto findUserById(Long id) {
        return userRepository.findById(id)
                .map(userReturnMapper::mapTo)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not exist"));
    }

    @Override
    public void updateUserData(Long userId, @NotNull UpdateDataRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + "not exist"));

        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UserNotFoundException("Need to be logged in"));

        if(!currentUser.equals(user)) {
            throw new AccessDeniedException("You are not authorized to update this user data");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password doesn't match");
        }

        user.setEmail(request.getData().getEmail());
        user.setFirstName(request.getData().getFirstName());
        user.setLastName(request.getData().getLastName());
        user.setPassword(passwordEncoder.encode(request.getData().getPassword()));

        userRepository.save(user);
    }
}
