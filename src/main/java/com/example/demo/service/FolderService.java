package com.example.demo.service;

import com.example.demo.model.Folder;
import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import com.example.demo.payload.request.FolderRequest;
import com.example.demo.payload.response.FolderResponse;
import com.example.demo.repository.FolderRepository;
import com.example.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserService userService;

    public FolderResponse createFolder(FolderRequest folderRequest) {
        User currentUser = userService.getCurrentUser();
        Folder folder = new Folder();
        folder.setName(folderRequest.getName());
        folder.setUser(currentUser);
        folderRepository.save(folder);
        FolderResponse response = new FolderResponse();
        response.setId(folder.getId());
        response.setName(folder.getName());
        return response;
    }

    public List<Folder> findByUser(User currentUser) {
        List<Folder> list = folderRepository.findByUser(currentUser);
        return list;
    }
}
