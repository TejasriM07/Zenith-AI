package com.zenith.ai.controller;

import com.zenith.ai.model.User;
import com.zenith.ai.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record SignupRequest(String name, String email, String password, String registeredBy) {}
    public record LoginRequest(String email, String password) {}

    @PostMapping("/signup")
    public ResponseEntity<?> registerCustomer(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole("ROLE_CUSTOMER");
        user.setAccessGranted(true);
        userRepository.save(user);
        return ResponseEntity.ok("Customer registered successfully!");
    }

    @PostMapping("/register-agent")
    public ResponseEntity<?> registerAgent(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        User user = new User();
        user.setName(request.name() != null ? request.name() : "CSR Employee");
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole("ROLE_AGENT");
        user.setRegisteredBy(request.registeredBy() != null ? request.registeredBy() : "Self / Public");
        user.setAccessGranted(false); // Requires Manager approval!
        userRepository.save(user);
        return ResponseEntity.ok("CSR Account registered successfully! Awaiting Manager Access Grant.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/toggle-access/{id}")
    public ResponseEntity<?> toggleAccess(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        User user = userOpt.get();
        user.setAccessGranted(!user.isAccessGranted());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.password())) {
            return ResponseEntity.status(401).body("Invalid email or password!");
        }
        User user = userOpt.get();
        if (user.getRole().equals("ROLE_AGENT") && !user.isAccessGranted()) {
            return ResponseEntity.status(403).body("Access Denied: Your manager has not yet granted you access.");
        }
        return ResponseEntity.ok(user);
    }
}