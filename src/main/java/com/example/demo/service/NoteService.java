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
import java.util.stream.IntStream;


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
        User currentUser = userService.getCurrentUser();
        note.setPosition(noteRepository.findByUser(currentUser).size()+1);
        note.setImageUrls(fileStorageService.saveFiles(files,currentUser.getId()));
        note.setUser(currentUser);
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
    @SneakyThrows
    public void updateOrder(List<String> orderedIds) {
        User currentUser = userService.getCurrentUser();
        List<Note> orderedNotes = orderedIds.stream()
                .map(idStr -> {
                    int noteId = Integer.parseInt(idStr);
                     Note note = getOrElseThrow(noteId);
                    if (!note.getUser().equals(currentUser)) {
                        throw new RuntimeException("Нет доступа к заметке с id " + noteId);
                    }
                    return note;
                })
                .toList();
        List<Integer> sortedPositions = orderedNotes.stream()
                .map(Note::getPosition)
                .sorted()
                .toList();
        final List<Note> updatedNotes = IntStream.range(0, orderedNotes.size())
                .mapToObj(i -> {
                    Note note = orderedNotes.get(i);
                    note.setPosition(sortedPositions.get(i));
                    return note;
                })
                .toList();
        noteRepository.saveAll(updatedNotes);
    }
}
