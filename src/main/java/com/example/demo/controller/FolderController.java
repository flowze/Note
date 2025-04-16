package com.example.demo.controller;

import com.example.demo.payload.request.FolderRequest;
import com.example.demo.payload.response.FolderResponse;
import com.example.demo.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/folders")
@Tag(name = "Папки", description = "Контроллер для управления папками")
public class FolderController {
    @Autowired
    FolderService folderService;

    @Operation(summary = "Создание новой папки",
                description = "Создает новую папку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Папка успешно создана",
                    content = @Content(schema = @Schema(implementation = FolderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<FolderResponse> addFold(@RequestBody FolderRequest folderRequest){
        FolderResponse response = folderService.createFolder(folderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
