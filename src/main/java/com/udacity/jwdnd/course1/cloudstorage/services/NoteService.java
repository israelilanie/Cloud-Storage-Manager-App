package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotesByUserId(Integer userId) {
        return noteMapper.getNotesByUserId(userId);
    }

    public boolean save(Note note) {
        if (note.getNoteId() == null) {
            return noteMapper.insert(note) > 0;
        }

        Note existingNote = noteMapper.getNoteById(note.getNoteId());
        if (existingNote == null || !existingNote.getUserId().equals(note.getUserId())) {
            return false;
        }

        return noteMapper.update(note) > 0;
    }

    public boolean delete(Integer noteId, Integer userId) {
        return noteMapper.delete(noteId, userId) > 0;
    }
}
