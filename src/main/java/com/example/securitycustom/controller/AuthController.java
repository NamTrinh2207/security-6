package com.example.securitycustom.controller;

import com.example.securitycustom.model.ERole;
import com.example.securitycustom.model.RefreshToken;
import com.example.securitycustom.model.Role;
import com.example.securitycustom.model.User;
import com.example.securitycustom.model.dto.request.SignInRequest;
import com.example.securitycustom.model.dto.request.SignupRequest;
import com.example.securitycustom.model.dto.request.TokenRefreshRequest;
import com.example.securitycustom.model.dto.response.JwtResponse;
import com.example.securitycustom.model.dto.response.MessageResponse;
import com.example.securitycustom.model.dto.response.TokenRefreshResponse;
import com.example.securitycustom.repository.RoleRepository;
import com.example.securitycustom.repository.UserRepository;
import com.example.securitycustom.security.JwtUtils;
import com.example.securitycustom.security.UserDetailsImpl;
import com.example.securitycustom.security.exception.TokenRefreshException;
import com.example.securitycustom.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@RestController
@Validated
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RoleRepository repository, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleRepository = repository;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return new ResponseEntity<>("Error:  Username is already taken! ", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Error: Email is already in use!"), HttpStatus.BAD_REQUEST);
        }
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword())).build();
        Set<String> roleStr = userRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (roleStr == null) {
            Role role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
                    () -> new RuntimeException("Error : Role is not found"));
            roles.add(role);
        } else {
            roleStr.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found")
                        );
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modnRole = roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found")
                        );
                        roles.add(modnRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found")
                        );
                        roles.add(userRole);
                    }
                }
            });
            user.setRoles(roles);
            userRepository.save(user);
        }
        return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.CREATED);
    }


    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignInRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(), userRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).toList();
        RefreshToken refreshToken = refreshTokenService.checkExistingToken(userDetails.getId());
        Date now = Date.from(Instant.now());
        if (refreshToken != null && refreshToken.getExpiryDate().isAfter(now.toInstant())) {
            return new ResponseEntity<>(new JwtResponse(
                    userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles, jwt, refreshToken.getToken()),
                    HttpStatus.OK);

        }else if (refreshToken == null){
            refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            assert refreshToken != null;
            return new ResponseEntity<>(new JwtResponse(
                    userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles, jwt, refreshToken.getToken()),
                    HttpStatus.OK);
        }
        else {
           return new ResponseEntity<>(new MessageResponse("Your account has expired!"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

}
