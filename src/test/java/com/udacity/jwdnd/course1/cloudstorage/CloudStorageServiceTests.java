package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CloudStorageServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private CredentialService credentialService;

    @Test
    void noteUpdatePersists() {
        Integer userId = createUser("svcNote");

        Note note = new Note();
        note.setUserId(userId);
        note.setNoteTitle("Initial");
        note.setNoteDescription("Initial Desc");
        Assertions.assertTrue(noteService.save(note));

        Note stored = noteService.getNotesByUserId(userId).getFirst();
        stored.setNoteTitle("Updated");
        stored.setNoteDescription("Updated Desc");
        Assertions.assertTrue(noteService.save(stored));

        Note updated = noteService.getNotesByUserId(userId).getFirst();
        Assertions.assertEquals("Updated", updated.getNoteTitle());
        Assertions.assertEquals("Updated Desc", updated.getNoteDescription());
    }

    @Test
    void credentialUpdatePersists() {
        Integer userId = createUser("svcCred");

        Credential credential = new Credential();
        credential.setUserId(userId);
        credential.setUrl("https://example.com");
        credential.setUsername("user1");
        credential.setPassword("pass1");
        Assertions.assertTrue(credentialService.save(credential));

        Credential stored = credentialService.getCredentialsByUserId(userId).getFirst();
        stored.setUrl("https://updated.example.com");
        stored.setUsername("user2");
        stored.setPassword("pass2");
        Assertions.assertTrue(credentialService.save(stored));

        Credential updated = credentialService.getCredentialsByUserId(userId).getFirst();
        Assertions.assertEquals("https://updated.example.com", updated.getUrl());
        Assertions.assertEquals("user2", updated.getUsername());
        Assertions.assertEquals("pass2", updated.getDecryptedPassword());
    }

    private Integer createUser(String prefix) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        String suffix = String.valueOf(System.nanoTime());
        String combined = prefix + suffix;
        user.setUsername(combined.substring(0, Math.min(combined.length(), 20)));
        user.setPassword("password");
        Assertions.assertTrue(userService.createUser(user));
        return userService.getUserId(user.getUsername());
    }
}
