package com.example.demo.controller;

import com.example.demo.service.NoteService;
import com.example.demo.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
@AllArgsConstructor
public class HomeController {
    @Autowired
    private final NoteService noteService;

    @Autowired
    private final UserService userService;

    @GetMapping("")
    public String homePage(){
        return "home";
    }

}
