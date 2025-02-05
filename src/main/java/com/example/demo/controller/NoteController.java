package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.payload.request.ReorderRequest;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.NoteService;
import com.example.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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
public class NoteController {
    private final NoteService noteService;

    private final UserService userService;
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @GetMapping("")
    public String pict(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model){
        model.addAttribute("notes", noteService.findByUser(userService.getCurrentUser()));
        return "notes";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("note", new Note());
        return "note-form";
    }

    @PostMapping("/new")
    public String createNote(@ModelAttribute("note") Note note,
                             @RequestParam("files") List<MultipartFile> files){
        noteService.createNote(note,files);
        return "redirect:/notes";
    }

    @GetMapping("/edit/{id}")
    private String editNoteForm(@PathVariable("id") int id, Model model){
        model.addAttribute("note", noteService.findNoteForEditing(id));
        return "edit-form";
    }

    @PostMapping("/edit/{id}")
    private String editNote(@PathVariable("id") int id,@ModelAttribute("note") Note updatedNote,
                            @RequestParam("files") List<MultipartFile> files){
        noteService.updateNote(id, updatedNote,files);
        return "redirect:/notes";
    }

    @PostMapping("/delete/{id}")
    private String deleteNote(@PathVariable("id") int id){
        noteService.deleteNote(id);
        return "redirect:/notes";
    }

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
