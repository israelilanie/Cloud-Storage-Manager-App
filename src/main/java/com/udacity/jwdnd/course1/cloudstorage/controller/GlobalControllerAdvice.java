package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(Model model) {
        model.addAttribute("success", false);
        model.addAttribute("message", "The selected file exceeds the 1MB upload limit.");
        model.addAttribute("tab", "files");
        return "result";
    }

    @ExceptionHandler(IOException.class)
    public String handleIoException(Model model) {
        model.addAttribute("success", false);
        model.addAttribute("message", "The requested file operation could not be completed.");
        model.addAttribute("tab", "files");
        return "result";
    }
}
