package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.FileRecord;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/file")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile multipartFile,
                             Authentication authentication,
                             Model model) throws IOException {
        String errorMessage = fileService.save(multipartFile, userService.getUserId(authentication.getName()));
        boolean success = errorMessage == null;
        model.addAttribute("success", success);
        model.addAttribute("message", success ? "File uploaded successfully." : errorMessage);
        model.addAttribute("tab", "files");
        return "result";
    }

    @GetMapping("/file/view/{fileId}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Integer fileId, Authentication authentication) {
        FileRecord fileRecord = fileService.getFile(fileId, userService.getUserId(authentication.getName()));
        if (fileRecord == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = fileRecord.getContentType() == null || fileRecord.getContentType().isBlank()
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(fileRecord.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileRecord.getFilename() + "\"")
                .body(fileRecord.getFileData());
    }

    @PostMapping("/file/delete")
    public String deleteFile(@RequestParam("fileId") Integer fileId, Authentication authentication, Model model) {
        boolean success = fileService.delete(fileId, userService.getUserId(authentication.getName()));
        model.addAttribute("success", success);
        model.addAttribute("message", success ? "File deleted successfully." : "Unable to delete file.");
        model.addAttribute("tab", "files");
        return "result";
    }
}
