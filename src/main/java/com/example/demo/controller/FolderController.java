package com.example.demo.controller;

import com.example.demo.payload.request.FolderRequest;
import com.example.demo.payload.response.FolderResponse;
import com.example.demo.repository.FolderRepository;
import com.example.demo.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/folders")
public class FolderController {
    @Autowired
    FolderService folderService;
    @PostMapping("")
    public ResponseEntity<FolderResponse> addFold(@RequestBody FolderRequest folderRequest){
        FolderResponse response = folderService.createFolder(folderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
