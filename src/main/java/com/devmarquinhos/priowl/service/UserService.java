package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Este e-mail já foi cadastrado. Tente outro.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return tokenService.generateToken(user);
    }
}
