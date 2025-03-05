package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteFolderService {
    private final NoteService noteService;
    private final FolderService folderService;

}
