package com.example.demo.repository;

import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository  extends JpaRepository<Note,Integer> {
    List<Note> findByUser(User user);
}
