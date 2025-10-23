package com.example.blackjack.config;

import com.example.blackjack.model.Role;
import com.example.blackjack.model.User;
import com.example.blackjack.model.game.GameState;
import com.example.blackjack.repository.RoleRepository;
import com.example.blackjack.repository.UserRepository;
import com.example.blackjack.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GameService gameService;

    @Override
    public void run(String... args) throws Exception {
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(userRole, adminRole));
            userRepository.save(admin);
            System.out.println("Created default admin user.");
        }

        for (int i = 1; i <= 5; i++) {
            GameState room = new GameState("Table " + i);

            gameService.registerGame(room);
        }
    }
}