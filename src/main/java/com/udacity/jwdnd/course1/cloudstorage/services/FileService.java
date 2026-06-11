package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.FileRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<FileRecord> getFilesByUserId(Integer userId) {
        return fileMapper.getFilesByUserId(userId);
    }

    public FileRecord getFile(Integer fileId, Integer userId) {
        FileRecord fileRecord = fileMapper.getFileById(fileId);
        if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
            return null;
        }
        return fileRecord;
    }

    public String save(MultipartFile multipartFile, Integer userId) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return "Please select a file to upload.";
        }

        if (fileMapper.getFileByName(userId, multipartFile.getOriginalFilename()) != null) {
            return "A file with that name already exists.";
        }

        FileRecord fileRecord = new FileRecord();
        fileRecord.setFilename(multipartFile.getOriginalFilename());
        fileRecord.setContentType(multipartFile.getContentType());
        fileRecord.setFileSize(String.valueOf(multipartFile.getSize()));
        fileRecord.setUserId(userId);
        fileRecord.setFileData(multipartFile.getBytes());
        fileMapper.insert(fileRecord);
        return null;
    }

    public boolean delete(Integer fileId, Integer userId) {
        return fileMapper.delete(fileId, userId) > 0;
    }
}
