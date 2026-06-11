package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    private final UserService userService;
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(UserService userService, FileService fileService, NoteService noteService, CredentialService credentialService) {
        this.userService = userService;
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
    }

    @GetMapping({"/", "/home"})
    public String homeView(Authentication authentication,
                           @RequestParam(name = "tab", defaultValue = "files") String activeTab,
                           Model model) {
        Integer userId = userService.getUserId(authentication.getName());
        model.addAttribute("files", fileService.getFilesByUserId(userId));
        model.addAttribute("notes", noteService.getNotesByUserId(userId));
        model.addAttribute("credentials", credentialService.getCredentialsByUserId(userId));
        model.addAttribute("activeTab", activeTab);
        return "home";
    }
}
