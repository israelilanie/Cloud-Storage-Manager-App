package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CredentialController {
    private final CredentialService credentialService;
    private final UserService userService;

    public CredentialController(CredentialService credentialService, UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping("/credential")
    public String saveCredential(@ModelAttribute Credential credential, Authentication authentication, Model model) {
        credential.setUserId(userService.getUserId(authentication.getName()));
        boolean success = credentialService.save(credential);
        populateResult(model, success, success ? "Credential saved successfully." : "Unable to save credential.", "credentials");
        return "result";
    }

    @PostMapping("/credential/delete")
    public String deleteCredential(@RequestParam("credentialId") Integer credentialId,
                                   Authentication authentication,
                                   Model model) {
        boolean success = credentialService.delete(credentialId, userService.getUserId(authentication.getName()));
        populateResult(model, success, success ? "Credential deleted successfully." : "Unable to delete credential.", "credentials");
        return "result";
    }

    private void populateResult(Model model, boolean success, String message, String tab) {
        model.addAttribute("success", success);
        model.addAttribute("message", message);
        model.addAttribute("tab", tab);
    }
}
