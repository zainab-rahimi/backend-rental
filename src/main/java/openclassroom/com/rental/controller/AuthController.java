package openclassroom.com.rental.controller;

import openclassroom.com.rental.dto.AuthRequest;
import openclassroom.com.rental.dto.AuthResponse;
import openclassroom.com.rental.dto.RegisterRequest;
import openclassroom.com.rental.dto.auth.LoginRequest;
import openclassroom.com.rental.dto.user.UserResponse;
import openclassroom.com.rental.entity.User;
import openclassroom.com.rental.repository.CustomUserDetailsService;
import openclassroom.com.rental.repository.UserRepository;
import openclassroom.com.rental.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        if (req.getName() == null || req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest()
            .body(Map.of("message", "Name, email, and password are required"));
        }
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Email is already in use"));
        }
        if (userRepository.findByName(req.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Name is already in use"));
        }
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("message","User registered successfully"));
    }




    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken (@RequestBody LoginRequest authRequest)
    {
        logger.debug("Login attempt for email: {}", authRequest.getEmail());

        if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
            logger.warn("Login attempt with null email or password");
            return ResponseEntity.badRequest()
                    .body(Map.of("message","Email and password are required"));
        }
        try {
            logger.debug("Attempting authentication for: {}", authRequest.getEmail());
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                    authRequest.getPassword())
            );
            logger.debug("Authentication successful for: {}", authRequest.getEmail());
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for email: {}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Incorrect email or password"));
        } catch (Exception e) {
            logger.error("Authentication error for email: {}", authRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","Authentication error: " + e.getMessage()));
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails);
        logger.debug("JWT token generated successfully for: {}", authRequest.getEmail());
        return  ResponseEntity.ok(new AuthResponse(jwt));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);

    }

}

