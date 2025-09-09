package com.student.auth;

import com.student.auth.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository repo;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserAccountRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void register(RegisterRequest req) {
        // 1) block duplicates
        if (repo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        // 2) make a new user with a scrambled (hashed) password
        UserAccount u = new UserAccount();
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password())); // <-- HASHING here
        // roles stays default "ROLE_STUDENT"
        repo.save(u);
    }
}
