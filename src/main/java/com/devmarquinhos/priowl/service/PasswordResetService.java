package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.model.PasswordResetToken;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.PasswordResetTokenRepository;
import com.devmarquinhos.priowl.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void generateResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // checks if the email exists, silent response due security breach
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        // delete older tokens
        tokenRepository.deleteByUser(user);

        // generates a new token and stores in db
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // send email to user
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("This link expired.");
        }

        // update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete token
        tokenRepository.delete(resetToken);
    }
}
