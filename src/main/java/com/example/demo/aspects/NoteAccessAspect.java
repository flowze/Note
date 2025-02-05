package com.example.demo.aspects;

import com.example.demo.model.Note;
import com.example.demo.model.user.User;
import com.example.demo.service.NoteService;
import com.example.demo.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
public class NoteAccessAspect {

    private final NoteService noteService;
    private final UserService userService;

    @Around("@annotation(checkNoteAccess)")
    @SneakyThrows
    public Object checkAccess(ProceedingJoinPoint joinPoint,
                              CheckNoteAccess checkNoteAccess){
        String idParamName = checkNoteAccess.noteIdParam();
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        int noteId = Arrays.stream(paramNames)
                .filter(paramName -> paramName.equals(checkNoteAccess.noteIdParam()))
                .findFirst()
                .map(foundName ->{
                    return (int) args[Arrays.asList(paramNames).indexOf(foundName)];
                })
                .orElseThrow(()-> new IllegalArgumentException("Параметр с id заметки не найден"));
        Note note = noteService.findById(noteId)
                .orElseThrow(()-> new EntityNotFoundException("Заметка не найдена"));
        User currentUser = userService.getCurrentUser();

        if (!note.getUser().equals(currentUser)) {
            throw new AccessDeniedException("Вы не можете редактировать чужую заметку");
        }

        return joinPoint.proceed();
    }
}
