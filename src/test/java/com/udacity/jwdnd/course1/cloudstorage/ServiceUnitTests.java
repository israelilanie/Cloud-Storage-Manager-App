package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.FileRecord;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.HashService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServiceUnitTests {
    @Test void noteSaveGuardsOwnershipAndDeletes() {
        NoteMapper mapper = mock(NoteMapper.class); NoteService service = new NoteService(mapper);
        Note note = note(1, 7); when(mapper.getNoteById(1)).thenReturn(note); when(mapper.update(note)).thenReturn(1);
        assertTrue(service.save(note)); when(mapper.getNotesByUserId(7)).thenReturn(List.of(note));
        assertEquals(List.of(note), service.getNotesByUserId(7));
        assertFalse(service.save(note(1, 8))); when(mapper.getNoteById(2)).thenReturn(null);
        assertFalse(service.save(note(2, 7))); when(mapper.insert(any())).thenReturn(1);
        assertTrue(service.save(note(null, 7))); when(mapper.delete(1, 7)).thenReturn(1); assertTrue(service.delete(1, 7));
    }

    @Test void fileServiceValidatesOwnershipAndUploadInput() throws Exception {
        FileMapper mapper = mock(FileMapper.class); FileService service = new FileService(mapper);
        FileRecord record = new FileRecord(); record.setUserId(7); record.setFileId(2);
        when(mapper.getFileById(2)).thenReturn(record); assertSame(record, service.getFile(2, 7)); assertNull(service.getFile(2, 8));
        assertEquals("Please select a file to upload.", service.save(null, 7));
        MockMultipartFile empty = new MockMultipartFile("fileUpload", new byte[0]); assertEquals("Please select a file to upload.", service.save(empty, 7));
        MockMultipartFile upload = new MockMultipartFile("fileUpload", "a.txt", "text/plain", "hi".getBytes());
        when(mapper.getFileByName(7, "a.txt")).thenReturn(record); assertEquals("A file with that name already exists.", service.save(upload, 7));
        when(mapper.getFileByName(7, "a.txt")).thenReturn(null); assertNull(service.save(upload, 7)); verify(mapper).insert(any(FileRecord.class));
        when(mapper.delete(2, 7)).thenReturn(1); assertTrue(service.delete(2, 7));
    }

    @Test void credentialServiceEncryptsDecryptsAndGuardsOwnership() {
        CredentialMapper mapper = mock(CredentialMapper.class); EncryptionService encryption = new EncryptionService();
        CredentialService service = new CredentialService(mapper, encryption); Credential credential = credential(1, 7, "secret");
        when(mapper.getCredentialById(1)).thenReturn(credential); when(mapper.update(credential)).thenReturn(1);
        assertTrue(service.save(credential)); assertNotEquals("secret", credential.getPassword()); assertEquals("secret", encryption.decryptValue(credential.getPassword(), credential.getKey()));
        when(mapper.getCredentialById(2)).thenReturn(null); assertFalse(service.save(credential(2, 7, "x")));
        assertFalse(service.save(credential(1, 8, "x"))); when(mapper.insert(any())).thenReturn(1); assertTrue(service.save(credential(null, 7, "new")));
        Credential stored = credential(null, 7, encryption.encryptValue("plain", "1234567890123456")); stored.setKey("1234567890123456");
        when(mapper.getCredentialsByUserId(7)).thenReturn(List.of(stored)); assertEquals("plain", service.getCredentialsByUserId(7).getFirst().getDecryptedPassword());
        when(mapper.delete(1, 7)).thenReturn(1); assertTrue(service.delete(1, 7));
    }

    @Test void userServiceHashesPasswordAndRejectsDuplicates() {
        UserMapper mapper = mock(UserMapper.class); HashService hashes = new HashService(); UserService service = new UserService(mapper, hashes);
        User user = new User(); user.setUsername("alice"); user.setPassword("password"); when(mapper.insert(user)).thenReturn(1);
        assertTrue(service.createUser(user)); assertNotEquals("password", user.getPassword()); assertEquals(user.getPassword(), hashes.getHashedValue("password", user.getSalt()));
        when(mapper.getUser("alice")).thenReturn(user); assertFalse(service.isUsernameAvailable("alice")); assertEquals(user.getUserId(), service.getUserId("alice")); assertNull(service.getUserId("none"));
        assertFalse(service.createUser(user));
    }
    private Note note(Integer id, int userId) { Note n = new Note(); n.setNoteId(id); n.setUserId(userId); return n; }
    private Credential credential(Integer id, int userId, String password) { Credential c = new Credential(); c.setCredentialId(id); c.setUserId(userId); c.setPassword(password); return c; }
}
