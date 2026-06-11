package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;
    private final SecureRandom secureRandom = new SecureRandom();

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getCredentialsByUserId(Integer userId) {
        List<Credential> credentials = credentialMapper.getCredentialsByUserId(userId);
        for (Credential credential : credentials) {
            credential.setDecryptedPassword(encryptionService.decryptValue(credential.getPassword(), credential.getKey()));
        }
        return credentials;
    }

    public boolean save(Credential credential) {
        if (credential.getCredentialId() != null) {
            Credential existingCredential = credentialMapper.getCredentialById(credential.getCredentialId());
            if (existingCredential == null || !existingCredential.getUserId().equals(credential.getUserId())) {
                return false;
            }
        }

        String encodedKey = generateKey();
        credential.setKey(encodedKey);
        credential.setPassword(encryptionService.encryptValue(credential.getPassword(), encodedKey));

        if (credential.getCredentialId() == null) {
            return credentialMapper.insert(credential) > 0;
        }

        return credentialMapper.update(credential) > 0;
    }

    public boolean delete(Integer credentialId, Integer userId) {
        return credentialMapper.delete(credentialId, userId) > 0;
    }

    private String generateKey() {
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key).substring(0, 16);
    }
}
