package com.example.demo.controller;

import com.example.demo.model.user.Role;
import com.example.demo.model.user.User;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.RegistrationRequest;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Аунтефикация", description = "Контроллер для аутентификации и регистрации пользователей")
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

    @Operation(summary = "Отображение страницы для нового логина", description = "Отправляет модель с пустым объектом LoginRequest для корректного отображения страницы нового логина.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница нового логина успешно загружена")
    })
    @GetMapping("newlogin")
    public String newlogin(Model model){
        model.addAttribute("account", new LoginRequest());
        return "auth/newlogin";
    }

    @Operation(summary = "Отображение страницы логина", description = "Отправляет модель с пустым объектом LoginRequest для корректного отображения страницы логина.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница логина успешно загружена")
    })
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("account", new LoginRequest());
        return "auth/login";
    }

    @Operation(summary = "Обработка логина", description = "Принимает LoginRequest, выполняет аутентификацию через AuthenticationService и перенаправляет на страницу заметок при успешной аутентификации, либо возвращает на страницу логина с ошибкой.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление при успешном/неуспешном логине")
    })
    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest){
        if(authenticationService.authenticate(loginRequest)){
            return "redirect:/notes";
        }
        return "redirect:/login?error";
    }

    @Operation(summary = "Отображение страницы регистрации", description = "Отправляет модель с пустым объектом LoginRequest (используется для регистрации) для корректного отображения страницы регистрации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница регистрации успешно загружена")
    })
    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("account" , new LoginRequest());
        return "auth/registration";
    }

    @Operation(summary = "Обработка регистрации", description = "Принимает RegistrationRequest, выполняет процесс регистрации через RegistrationService и перенаправляет на страницу логина.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на страницу логина после успешной регистрации")
    })
    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute(name = "account") RegistrationRequest registrationRequest){
        registrationService.register(registrationRequest);
        return "redirect:/auth/login";
    }
}
