package com.example.demo.controller;

import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.RegistrationRequest;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    RegistrationService registrationService;

    @GetMapping("newlogin")
    public String newlogin(Model model){
        model.addAttribute("account", new LoginRequest());
        return "auth/newlogin";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("account", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest){
        if(authenticationService.authenticate(loginRequest)){
            return "redirect:/notes";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("account" , new LoginRequest());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute(name = "account") RegistrationRequest registrationRequest){
        registrationService.register(registrationRequest);
        return "redirect:/auth/login";
    }
}
