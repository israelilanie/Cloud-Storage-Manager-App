package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controller.CredentialController;
import com.udacity.jwdnd.course1.cloudstorage.controller.FileController;
import com.udacity.jwdnd.course1.cloudstorage.controller.GlobalControllerAdvice;
import com.udacity.jwdnd.course1.cloudstorage.controller.HomeController;
import com.udacity.jwdnd.course1.cloudstorage.controller.LoginController;
import com.udacity.jwdnd.course1.cloudstorage.controller.NoteController;
import com.udacity.jwdnd.course1.cloudstorage.controller.SignupController;
import com.udacity.jwdnd.course1.cloudstorage.model.FileRecord;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.security.CloudStorageAuthenticationProvider;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerAndSecurityTests {
    @Test void noteAndCredentialControllersSetResultMessages() {
        NoteService notes = mock(NoteService.class); CredentialService credentials = mock(CredentialService.class); UserService users = mock(UserService.class);
        when(users.getUserId("user")).thenReturn(7); var auth = new UsernamePasswordAuthenticationToken("user", "password");
        when(notes.save(any())).thenReturn(true); ExtendedModelMap model = new ExtendedModelMap(); assertEquals("result", new NoteController(notes, users).saveNote(new Note(), auth, model)); assertEquals(true, model.get("success"));
        when(notes.delete(1, 7)).thenReturn(false); model = new ExtendedModelMap(); new NoteController(notes, users).deleteNote(1, auth, model); assertEquals("Unable to delete note.", model.get("message"));
        when(credentials.save(any())).thenReturn(false); model = new ExtendedModelMap(); new CredentialController(credentials, users).saveCredential(new com.udacity.jwdnd.course1.cloudstorage.model.Credential(), auth, model); assertEquals(false, model.get("success"));
        when(credentials.delete(1, 7)).thenReturn(true); model = new ExtendedModelMap(); new CredentialController(credentials, users).deleteCredential(1, auth, model); assertEquals("Credential deleted successfully.", model.get("message"));
    }

    @Test void fileControllerHandlesResponsesAndFailures() throws Exception {
        FileService files = mock(FileService.class); UserService users = mock(UserService.class); when(users.getUserId("user")).thenReturn(7); var auth = new UsernamePasswordAuthenticationToken("user", "password");
        FileController controller = new FileController(files, users); ExtendedModelMap model = new ExtendedModelMap(); when(files.save(any(), eq(7))).thenReturn(null);
        assertEquals("result", controller.uploadFile(new MockMultipartFile("fileUpload", "x.txt", "text/plain", new byte[]{1}), auth, model)); assertEquals(true, model.get("success"));
        model = new ExtendedModelMap(); when(files.delete(1, 7)).thenReturn(false); controller.deleteFile(1, auth, model); assertEquals(false, model.get("success"));
        assertEquals(HttpStatus.NOT_FOUND, controller.viewFile(1, auth).getStatusCode()); FileRecord record = new FileRecord(); record.setFilename("x.txt"); record.setContentType(""); record.setFileData(new byte[]{1}); when(files.getFile(1, 7)).thenReturn(record);
        assertEquals("application/octet-stream", controller.viewFile(1, auth).getHeaders().getContentType().toString());
    }

    @Test void signupHomeAndAdviceCoverViewsAndErrors() {
        UserService users = mock(UserService.class); SignupController signup = new SignupController(users); User user = new User(); user.setUsername("user"); ExtendedModelMap model = new ExtendedModelMap();
        when(users.isUsernameAvailable("user")).thenReturn(false); assertEquals("signup", signup.signupUser(user, model)); when(users.isUsernameAvailable("user")).thenReturn(true); when(users.createUser(user)).thenReturn(false); assertEquals("signup", signup.signupUser(user, new ExtendedModelMap())); when(users.createUser(user)).thenReturn(true); assertEquals("redirect:/login?signupSuccess", signup.signupUser(user, new ExtendedModelMap())); assertEquals("signup", signup.signupView()); assertEquals("login", new LoginController().loginView());
        assertEquals("result", new GlobalControllerAdvice().handleIoException(new ExtendedModelMap())); assertEquals("result", new GlobalControllerAdvice().handleMaxUploadSizeExceeded(new ExtendedModelMap()));
        FileService files = mock(FileService.class); NoteService notes = mock(NoteService.class); CredentialService credentials = mock(CredentialService.class); when(users.getUserId("user")).thenReturn(7); ExtendedModelMap home = new ExtendedModelMap(); assertEquals("home", new HomeController(users, files, notes, credentials).homeView(new UsernamePasswordAuthenticationToken("user", "p"), "notes", home)); assertEquals("notes", home.get("activeTab"));
    }

    @Test void authenticationProviderAcceptsValidPasswordOnly() {
        UserService users = mock(UserService.class); HashService hashes = new HashService(); User user = new User(); user.setSalt("salt"); user.setPassword(hashes.getHashedValue("password", "salt")); when(users.getUser("user")).thenReturn(user);
        CloudStorageAuthenticationProvider provider = new CloudStorageAuthenticationProvider(users, hashes); assertNotNull(provider.authenticate(new UsernamePasswordAuthenticationToken("user", "password"))); assertNull(provider.authenticate(new UsernamePasswordAuthenticationToken("user", "bad"))); assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class)); assertFalse(provider.supports(String.class));
    }
}
