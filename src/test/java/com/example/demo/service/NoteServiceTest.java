package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @Mock
    NoteRepository noteRepository;
    @Mock
    UserService userService;
    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    NoteService noteService;

    @Test
    void updateNote_shouldUpdateNoteSuccessfully() {
        int noteId = 1;

        User user = new User();
        user.setId(1);

        Note existingNote = new Note();
        existingNote.setId(noteId);
        existingNote.setTitle("Старый заголовок");
        existingNote.setContent("Старое содержание");
        existingNote.setUser(user);
        existingNote.setImageUrls(new ArrayList<>());

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));

        Note updatedNote = new Note();
        updatedNote.setTitle("Новый заголовок");
        updatedNote.setContent("Новое содержание");

        List<MultipartFile> files = Collections.emptyList();
        List<String> savedUrls = List.of("url1", "url2");
        when(fileStorageService.saveFiles(files, user.getId())).thenReturn(savedUrls);

        noteService.updateNote(noteId, updatedNote, files);

        assertEquals("Новый заголовок", existingNote.getTitle());
        assertEquals("Новое содержание", existingNote.getContent());
        assertEquals(savedUrls, existingNote.getImageUrls());
    }

    @Test
    void updateNote_shouldThrowEntityNotFoundException_whenNoteNotFound() {
        final int noteId = 1;
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        final Note updatedNote = new Note();
        updatedNote.setTitle("Новый заголовок");
        updatedNote.setContent("Новое содержание");

        final List<MultipartFile> files = Collections.emptyList();

        assertThrows(EntityNotFoundException.class, () -> noteService.updateNote(noteId, updatedNote, files));
    }
}