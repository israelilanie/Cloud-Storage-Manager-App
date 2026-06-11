package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("/note")
    public String saveNote(@ModelAttribute Note note, Authentication authentication, Model model) {
        note.setUserId(userService.getUserId(authentication.getName()));
        boolean success = noteService.save(note);
        populateResult(model, success, success ? "Note saved successfully." : "Unable to save note.", "notes");
        return "result";
    }

    @PostMapping("/note/delete")
    public String deleteNote(@RequestParam("noteId") Integer noteId, Authentication authentication, Model model) {
        boolean success = noteService.delete(noteId, userService.getUserId(authentication.getName()));
        populateResult(model, success, success ? "Note deleted successfully." : "Unable to delete note.", "notes");
        return "result";
    }

    private void populateResult(Model model, boolean success, String message, String tab) {
        model.addAttribute("success", success);
        model.addAttribute("message", message);
        model.addAttribute("tab", tab);
    }
}
