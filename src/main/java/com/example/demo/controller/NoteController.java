package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import com.example.demo.payload.request.ReorderRequest;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.FolderService;
import com.example.demo.service.NoteFolderService;
import com.example.demo.service.NoteService;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notes")
@Slf4j
@Tag(name = "Записки", description = "Контроллер для управления заметками")
public class NoteController {
    private final NoteService noteService;

    private final UserService userService;
    private final FolderService folderService;
    private final NoteFolderService noteFolderService;
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Operation(summary = "Отображение списка заметок", description = "Возвращает страницу заметок с фильтрацией по папке, если указан параметр folderId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница заметок успешно загружена", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("")
    public String pict(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
                       @RequestParam(required = false) String folderId){
        User currentUser = userService.getCurrentUser();
        model.addAttribute("notes", noteService.findByFolder(folderId));
        model.addAttribute("folders", folderService.findByUser(currentUser));
        return "notes";
    }

    @Operation(summary = "Отображение формы создания заметки", description = "Возвращает страницу с формой для создания новой заметки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница формы создания заметки успешно загружена", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/new")
    public String showCreateForm(Model model){
        User currentUser = userService.getCurrentUser();
        model.addAttribute("note", new Note());
        model.addAttribute("folders", folderService.findByUser(currentUser));
        return "note-form";
    }

    @Operation(summary = "Создание новой заметки", description = "Создаёт новую заметку с возможностью прикрепления файлов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на страницу заметок после создания заметки", content = @Content)
    })
    @PostMapping("/new")
    public String createNote(@ModelAttribute("note") Note note,
                             @RequestParam("files") List<MultipartFile> files){
        noteService.createNote(note,files);
        return "redirect:/notes";
    }

    @Operation(summary = "Отображение формы редактирования заметки", description = "Возвращает страницу с формой для редактирования заметки по заданному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница формы редактирования успешно загружена", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/edit/{id}")
    private String editNoteForm(@PathVariable("id") int id, Model model){
        model.addAttribute("note", noteService.findNoteForEditing(id));
        return "edit-form";
    }

    @Operation(summary = "Редактирование заметки", description = "Обновляет заметку с заданным ID и перенаправляет на страницу заметок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на страницу заметок после успешного обновления", content = @Content)
    })
    @PostMapping("/edit/{id}")
    private String editNote(@PathVariable("id") int id,@ModelAttribute("note") Note updatedNote,
                            @RequestParam("files") List<MultipartFile> files){
        noteService.updateNote(id, updatedNote,files);
        return "redirect:/notes";
    }


    @Operation(summary = "Удаление заметки", description = "Удаляет заметку с заданным ID и перенаправляет на страницу заметок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на страницу заметок после удаления", content = @Content)
    })
    @PostMapping("/delete/{id}")
    private String deleteNote(@PathVariable("id") int id){
        noteService.deleteNote(id);
        return "redirect:/notes";
    }

    @Operation(summary = "Удаление изображения из заметки",
            description = "Удаляет изображение из заметки, проверяя корректность пути загрузки. В случае нарушения доступа выбрасывается AccessDeniedException")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправление на страницу редактирования заметки после успешного удаления изображения", content = @Content),
            @ApiResponse(responseCode = "403", description = "Ошибка доступа при попытке удаления изображения с неверным путём загрузки", content = @Content)
    })
    @PostMapping("/{id}/deleteImage/{UPLOAD_DIR}/{img:.+}")
    @SneakyThrows
    private String deleteImage(@PathVariable("id") int noteId, @PathVariable("img") String ImgUrl
                               ,@PathVariable("UPLOAD_DIR") String upload){
        if(!upload.equals(UPLOAD_DIR)){
            throw new AccessDeniedException("");
        }
        noteService.deleteImage(noteId,UPLOAD_DIR+"/"+ImgUrl);
        return "redirect:/notes/edit/"+String.valueOf(noteId);
    }

    @Operation(summary = "Перестановка заметок", description = "Обновляет порядок заметок согласно переданному списку идентификаторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Порядок заметок успешно обновлен", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/reorder")
    @SneakyThrows
    public ResponseEntity<String> reorderNotes(@RequestBody final ReorderRequest reorderRequest) {
        List<String> orderedIds = reorderRequest.getOrderedIds()
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
        noteService.updateOrder(orderedIds);
        return ResponseEntity.ok("Порядок заметок успешно обновлен");
    }
}
