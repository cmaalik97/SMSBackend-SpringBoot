package com.sms.backend.service;

import com.sms.backend.dto.UserDto;
import com.sms.backend.dto.UserRequest;
import com.sms.backend.entity.Role;
import com.sms.backend.entity.User;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getUserId(), u.getFullName(), u.getEmail(), u.getRole().name(), u.isActive());
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public UserDto create(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("An account with this email already exists.");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("Password is required for a new account.");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(parseRole(req.getRole()));
        user.setActive(true);

        return toDto(userRepository.save(user));
    }

    public UserDto update(Long id, UserRequest req) {
        User user = findEntity(id);
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setRole(parseRole(req.getRole()));
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found.");
        }
        userRepository.deleteById(id);
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (Exception e) {
            throw new BadRequestException("Role must be Admin, Teacher or Student.");
        }
    }
}
