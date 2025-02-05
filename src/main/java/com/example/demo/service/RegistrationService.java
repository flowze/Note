package com.example.demo.service;

import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import com.example.demo.payload.request.RegistrationRequest;
import com.example.demo.service.user.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public void register(final RegistrationRequest registrationRequest){
        final User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.user);
        userService.save(user);
        log.info("Registered new user: {}", registrationRequest.getUsername());
    }


}
