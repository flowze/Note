package com.example.demo.service;

import com.example.demo.aspects.CheckNoteAccess;
import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;


    public void save(Note note) {
        noteRepository.save(note);
    }

    public List<Note> findByUser(User user) {

        List<Note> list = noteRepository.findByUser(user);
        list.sort(Comparator.comparingInt(Note::getPosition));
        return list;
    }

    public Optional<Note> findById(int id) {
        return noteRepository.findById(id);
    }

    @SneakyThrows
    @CheckNoteAccess(noteIdParam = "id")
    @Transactional
    public void updateNote(int id, Note updatedNote, List<MultipartFile> files) {
        Note existingNote = getOrElseThrow(id);

        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.getImageUrls().addAll(fileStorageService.saveFiles(files, existingNote.getUser().getId()));

        noteRepository.save(existingNote);
    }

    @SneakyThrows
    @CheckNoteAccess(noteIdParam = "id")
    public Note findNoteForEditing(int id) {
        return getOrElseThrow(id);
    }

    @SneakyThrows
    @CheckNoteAccess(noteIdParam = "id")
    public void deleteNote(int id) {
        Note existingNote = getOrElseThrow(id);
        fileStorageService.deleteFiles(existingNote.getImageUrls());
        noteRepository.delete(existingNote);
    }

    private Note getOrElseThrow(int id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заметка не найдена"));
    }

    public void createNote(Note note, List<MultipartFile> files) {
        User user = userService.getCurrentUser();
        note.setPosition(noteRepository.findByUser(user).size()+1);
        note.setImageUrls(fileStorageService.saveFiles(files,user.getId()));
        note.setUser(user);
        this.save(note);
    }

    @Transactional
    @CheckNoteAccess(noteIdParam = "id")
    public void deleteImage(int id, String s) {
        Note existingNote = getOrElseThrow(id);

        List<String> updatedUrls = new ArrayList<>(existingNote.getImageUrls());
        if (updatedUrls.remove(s)) {
            fileStorageService.deleteFiles(List.of(s));
            existingNote.setImageUrls(updatedUrls);
            noteRepository.save(existingNote);
        }
    }

    @Transactional
    public void updateOrder(List<String> orderedIds) {
        User user = userService.getCurrentUser();
        List<Note> allNotes = noteRepository.findByUser(user);
        Map<Integer, Note> noteMap = allNotes.stream()
                .collect(Collectors.toMap(Note::getPosition, note -> note));
        for(int i = 0;i<orderedIds.size();i++){
            int index = Integer.parseInt(orderedIds.get(i));
            Note note = noteMap.get(index);
            note.setPosition(i);
            noteRepository.save(note);
        }
    }
}
